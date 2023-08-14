package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.PlayerIconName;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.messaging.SeshUpdate;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class GameLogicServiceImpl implements GameLogicService {

    private final PlayerRepository playerRepository;
    private final StateRepository stateRepository;
    private final CommandRespository commandRespository;
    private final BroadcastService broadcastService;
    private final SeshService seshService;

    @Autowired
    public GameLogicServiceImpl(StateRepository stateRepository, CommandRespository commandRespository, BroadcastService stompBroadcastService, SeshService seshService, PlayerRepository playerRepository) {

        this.stateRepository = stateRepository;
        this.commandRespository = commandRespository;
        this.broadcastService = stompBroadcastService;
        this.seshService = seshService;
        this.playerRepository = playerRepository;
    }

    @Override
    public ControllerState joinAsController(String seshCode, Player player, String reconnectToken) {

        State state = seshService.getSesh(seshCode);
        boolean seshIsFull = state.getPlayers().size() == state.getMaxPlayer();
        boolean playerAlreadyJoined = playerRepository.existsByState_SeshCodeAndPlayerName(seshCode, player.getPlayerName());
        if ((seshIsFull || playerAlreadyJoined) && reconnectToken == null) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if(reconnectToken != null && playerAlreadyJoined){
            state.setHasChanged(true);
            stateRepository.save(state);
            return extractControllerState(state);
        }

        player.setState(state);
        player.setVip(false);
        player.setPoints(0L);
        player.setPlayerIconName(PlayerIconName.BASIC);
        playerRepository.save(player);
        state.getPlayers().add(player);
        state.setHasChanged(true);
        stateRepository.save(state);
        return extractControllerState(state);
    }

    @Override
    public HostState joinAsHost(String seshCode, String socketId, String reconnectToken) {

        State state = seshService.getSesh(seshCode);
        String hostId = state.getHostId();
        if (hostId != null && reconnectToken == null) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (reconnectToken != null && hostId.equals(reconnectToken)){
            
            return extractHostState(state);
        }

        state.setHostId(socketId);
        state.setHasChanged(true);
        stateRepository.save(state);
        return extractHostState(state);
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
        if (state.getHostId() == null) {

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

        if (isVip(state, command.getPlayerId()) && command.getType().equals("startSesh")) {

            processStartSeshCommand(state);

        } else if ((isVip(state, command.getPlayerId()) || !hasVip(state)) && command.getType().equals("makeVip")) {

            processMakeVipCommand(state, command.getPlayerId(), command.getBody());

        } else if (command.getType().equals("changeIcon")) {

            processChangeIconCommand(state, command.getPlayerId(), command.getBody());

        } else {

            throw new IllegalArgumentException();
        }
    }

    private void processMakeVipCommand(State state, String executerId, String targetId) {

        boolean targetIsValid = state.getPlayers().stream().anyMatch(player -> player.getPlayerId().equals(targetId));
        if (!targetIsValid) {

            return;
        }
        state.getPlayers().stream().filter(player -> player.getPlayerId().equals(executerId)).forEach(player -> player.setVip(false));
        state.getPlayers().stream().filter(player -> player.getPlayerId().equals(targetId)).forEach(player -> player.setVip(true));
    }

    private void processChangeIconCommand(State state, String playerId, String body) {

        if (Arrays.stream(PlayerIconName.values()).noneMatch(playerIconName -> playerIconName.name().equals(body))) {
            return;
        }
        List<Player> players = state.getPlayers().stream().filter(player -> player.getPlayerId().equals(playerId)).toList();
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

        if (!isVip(state, command.getPlayerId())) return;
        if ("next".equals(command.getBody())) state.nextQuestion();
        if ("prev".equals(command.getBody())) state.prevQuestion();

        state.setShowQuestion(false);
        state.setShowAnswer(false);
    }

    private void processShowQuestionCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerId())) return;
        boolean showQuestion = command.getBody().equals("true");
        state.setShowQuestion(showQuestion);
    }

    private void processShowAnswerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerId())) return;
        boolean showAnswer = command.getBody().equals("true");
        state.setShowAnswer(showAnswer);
        if (showAnswer) state.setShowQuestion(true);
    }

    private void processBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerId()) && isBuzzed(state)) return;
        if (!isVip(state, command.getPlayerId()) && !isBuzzed(state)) state.setBuzzedPlayerId(command.getPlayerId());
        if (isVip(state, command.getPlayerId())) state.setBuzzedPlayerId(command.getBody());
    }

    private void processFreeBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerId()) || !isBuzzed(state)) return;
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
                .filter(player -> !player.getPlayerId().equals(state.getBuzzedPlayerId()))
                .forEach(player -> player.addPoints(1));
                // @formatter:on
        freeBuzzer(state);

    }

    private void handleTrueAnswer(State state) {

        List<Player> players = state.getPlayers();
        int pointsToAward = players.size() - 2;
        players.parallelStream()
                // @formatter:off
                .filter(player -> player.getPlayerId().equals(state.getBuzzedPlayerId()))
                .forEach(player -> player.addPoints(pointsToAward));
                // @formatter:on
        freeBuzzer(state);
    }

    private void freeBuzzer(State state) {

        state.setBuzzedPlayerId(null);
    }

    private boolean isBuzzed(State state) {

        return state.getBuzzedPlayerId() != null;
    }

    private static boolean hasVip(State state) {

        return state.getPlayers().stream().anyMatch(Player::getVip);
    }

    private static boolean isVip(State state, String playerId) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().equals(playerId) && player.getVip());
    }

    private void broadcastState(State state) {

        HostState host = extractHostState(state);
        ControllerState controller = extractControllerState(state);
        SeshUpdate seshUpdate = new SeshUpdate(host, controller);

        try {
            broadcastService.broadcastSeshUpdate(seshUpdate, state.getSeshCode());
        } catch (NullPointerException e) {

            log.error("broadcastState has failed due to stomp session being null.");
        }

    }

    private ControllerState extractControllerState(State state) {

        HostState hostState = extractHostState(state);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(hostState, ControllerState.class);
    }

    private HostState extractHostState(State state) {

        HostState hostState = new HostState();
        hostState.setPlayers(state.getPlayers());
        hostState.setSeshCode(state.getSeshCode());
        hostState.setCurrentStage(state.getSeshStage());

        if (state.getSeshStage() == SeshStage.LOBBY) {

            hostState.setMaxPlayers(state.getMaxPlayer());
            hostState.setHasVip(hasVip(state));

        } else if (state.getSeshStage() == SeshStage.MAIN) {

            Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
            hostState.setCurrentQuestion(currentQuestion);
            hostState.setShowQuestion(state.getShowQuestion());
            hostState.setShowAnswer(state.getShowAnswer());
            hostState.setBuzzedPlayerId(state.getBuzzedPlayerId());
        }

        return hostState;
    }

}
