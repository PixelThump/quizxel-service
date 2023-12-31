package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.QuestionPackRepository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class GameLogicServiceImpl implements GameLogicService {

    private final StateRepository stateRepository;
    private final CommandRespository commandRespository;
    private final BroadcastService broadcastService;
    private final QuestionPackRepository questionPackRepository;

    @Autowired
    public GameLogicServiceImpl(StateRepository stateRepository, CommandRespository commandRespository, BroadcastService stompBroadcastService, QuestionPackRepository questionPackRepository) {

        this.stateRepository = stateRepository;
        this.commandRespository = commandRespository;
        this.broadcastService = stompBroadcastService;
        this.questionPackRepository = questionPackRepository;
    }

    @Scheduled(fixedDelayString = "${quizxel.tickrate}", initialDelayString = "${quizxel.tickrate}")
    public void processQueues() {

        LocalDateTime startTime = LocalDateTime.now();
        log.debug("starting processQueues at {}", startTime);
        List<State> states = stateRepository.findByActive(true);
        if (states.isEmpty()) return;
        states.parallelStream().forEach(state -> updateState(state.getSeshCode()));
        LocalDateTime endTime = LocalDateTime.now();
        log.debug("Finished processQueues at {} took {} ms", endTime, ChronoUnit.MILLIS.between(startTime, endTime));
    }

    @Transactional
    private void updateState(String seshCode) {

        State state = stateRepository.findBySeshCode(seshCode);
        if (!state.isHostJoined()) {

            return;
        }

        List<Command> successfullyProcessedCommands = processCommands(state);
        log.debug("SuccessfullyProcessedCommands={}", successfullyProcessedCommands);

        if (Boolean.FALSE.equals(state.getHasChanged())) {
            return;
        }

        state.setHasChanged(false);
        broadcastState(state);
        stateRepository.save(state);
    }

    private List<Command> processCommands(State state) {

        List<Command> commands = commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode());
        List<Command> successfullyProcessedCommands = new ArrayList<>();
        List<Command> processedCommands = new ArrayList<>();

        for (Command command : commands) {

            try {
                boolean commandProcessedSuccessfully = processCommand(state, command);
                if (commandProcessedSuccessfully) successfullyProcessedCommands.add(command);
                processedCommands.add(command);
            } catch (Exception e) {
                processedCommands.add(command);
                log.warn("Unable to process command={}", command);
            }
        }
        commandRespository.deleteAll(processedCommands);
        return successfullyProcessedCommands;
    }

    private boolean processCommand(State state, Command command) {

        if (state.getSeshStage() == SeshStage.LOBBY) {

            processLobbyStageCommand(state, command);
            state.setHasChanged(true);
            return true;

        } else if (state.getSeshStage() == SeshStage.MAIN) {

            processMainStageCommand(state, command);
            state.setHasChanged(true);
            return false;
        }
        return false;
    }

    private void processLobbyStageCommand(State state, Command command) {

        if (isVip(state, command.getPlayerName()) && command.getType().equals("startSesh")) {

            processStartSeshCommand(state);

        } else if ((isVip(state, command.getPlayerName()) || !hasVip(state)) && command.getType().equals("makeVip")) {

            processMakeVipCommand(state, command.getPlayerName(), command.getBody());

        } else if (command.getType().equals("changeIcon")) {

            processChangeIconCommand(state, command.getPlayerName(), command.getBody());

        } else if (command.getType().equals("changeQuestionPack")) {

            processchangeQuestionPackCommand(state, command.getPlayerName(), command.getBody());

        } else {

            throw new IllegalArgumentException();
        }
    }

    private void processchangeQuestionPackCommand(State state, String playerName, String body) {

        if (!isVip(state, playerName)) {

            return;
        }

        Optional<Questionpack> questionPackOptional = questionPackRepository.findByPackName(body);
        if (questionPackOptional.isEmpty()){

            return;
        }
        state.setSelectedQuestionPack(questionPackOptional.get());
    }

    private void processMakeVipCommand(State state, String executerName, String targetName) {

        boolean targetIsValid = state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(targetName));
        if (!targetIsValid) {

            return;
        }
        state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(executerName)).forEach(player -> player.setVip(false));
        state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(targetName)).forEach(player -> player.setVip(true));
    }

    private void processChangeIconCommand(State state, String playerName, String body) {

        if (Arrays.stream(PlayerIconName.values()).noneMatch(playerIconName -> playerIconName.name().equals(body))) {
            return;
        }
        List<Player> players = state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(playerName)).toList();
        if (players.size() != 1) {
            return;
        }
        PlayerIconName playerIconName = PlayerIconName.valueOf(body);
        Player player = players.get(0);
        player.setPlayerIconName(playerIconName);

    }

    private void processStartSeshCommand(State state) {

        state.setCurrentQuestionIndex(0L);
        state.setSeshStage(SeshStage.MAIN);
    }

    private void processMainStageCommand(State state, Command command) {

        String actionType = command.getType();
        switch (actionType) {

            case "nextQuestion" -> processNextQuestionCommand(state, command);
            case "showQuestion" -> processShowQuestionCommand(state, command);
            case "showAnswer" -> processShowAnswerCommand(state, command);
            case "buzzer" -> processBuzzerCommand(state, command);
            case "freeBuzzer" -> processFreeBuzzerCommand(state, command);
            default -> log.error("Got a command without a valid Action type. Action={}", command);
        }
    }

    private void processNextQuestionCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerName())) return;
        if ("next".equals(command.getBody())) state.nextQuestion();
        if ("prev".equals(command.getBody())) state.prevQuestion();

        state.setShowQuestion(false);
        state.setShowAnswer(false);
    }

    private void processShowQuestionCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerName())) return;
        boolean showQuestion = command.getBody().equals("true");
        state.setShowQuestion(showQuestion);
    }

    private void processShowAnswerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerName())) return;
        boolean showAnswer = command.getBody().equals("true");
        state.setShowAnswer(showAnswer);
        if (showAnswer) state.setShowQuestion(true);
    }

    private void processBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerName()) && isBuzzed(state)) return;
        if (!isVip(state, command.getPlayerName()) && !isBuzzed(state))
            state.setBuzzedPlayerName(command.getPlayerName());
        if (isVip(state, command.getPlayerName())) state.setBuzzedPlayerName(command.getBody());
    }

    private void processFreeBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerName()) || !isBuzzed(state)) return;
        String commandBody = command.getBody();
        if (commandBody == null) {
            freeBuzzer(state);
        } else if (commandBody.equals("correct")) {
            handleTrueAnswer(state);
        } else if (commandBody.equals("wrong")) {
            handleWrongAnswer(state);
        } else {
            freeBuzzer(state);
        }
    }

    private void handleWrongAnswer(State state) {

        List<Player> players = state.getPlayers();
        players.parallelStream()
                // @formatter:off
                .filter(player -> !player.getPlayerId().getPlayerName().equals(state.getBuzzedPlayerName()))
                .forEach(player -> player.addPoints(1));
                // @formatter:on
        freeBuzzer(state);

    }

    private void handleTrueAnswer(State state) {

        List<Player> players = state.getPlayers();
        int pointsToAward = players.size() - 2;
        players.parallelStream()
                // @formatter:off
                .filter(player -> player.getPlayerId().getPlayerName().equals(state.getBuzzedPlayerName()))
                .forEach(player -> player.addPoints(pointsToAward));
                // @formatter:on
        freeBuzzer(state);
    }

    private void freeBuzzer(State state) {

        state.setBuzzedPlayerName(null);
    }

    private boolean isBuzzed(State state) {

        return state.getBuzzedPlayerName() != null;
    }

    private static boolean hasVip(State state) {

        return state.getPlayers().stream().anyMatch(Player::getVip);
    }

    private static boolean isVip(State state, String playerName) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName) && player.getVip());
    }

    private void broadcastState(State state) {

        try {
            broadcastService.broadcastSeshUpdate(state);
        } catch (NullPointerException e) {

            log.error("broadcastState has failed due to stomp session being null.");
        }

    }
}
