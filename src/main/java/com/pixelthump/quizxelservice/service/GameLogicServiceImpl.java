package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.service.model.messaging.SeshUpdate;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
    public GameLogicServiceImpl(StateRepository stateRepository, CommandRespository commandRespository, BroadcastService broadcastService, SeshService seshService, PlayerRepository playerRepository) {

        this.stateRepository = stateRepository;
        this.commandRespository = commandRespository;
        this.broadcastService = broadcastService;
        this.seshService = seshService;
        this.playerRepository = playerRepository;
    }

    @Scheduled(fixedDelayString = "${quizxel.tickrate}", initialDelayString = "${quizxel.tickrate}")
    public void processQueues() {

        List<State> states = stateRepository.findByActive(true);
        states.parallelStream().forEach(this::processQueue);
    }

    private void processQueue(State state) {

        if (state.getHostId() == null) {

            return;
        }

        List<Command> commands = commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode());

        for (Command command : commands) {

            try {

                processCommand(state, command);
                commandRespository.deleteByCommandId(command.getCommandId());
            } catch (Exception e) {
                commandRespository.deleteByCommandId(command.getCommandId());
                log.warn("Unable to process command={}", command);
            }
        }
        broadcastState(state);
    }

    private State processCommand(State state, Command command) {

        State newState;

        if (state.getSeshStage() == SeshStage.LOBBY) {

            newState = processLobbyStageCommand(state, command);
            stateRepository.save(newState);
            return newState;

        } else if (state.getSeshStage() == SeshStage.MAIN) {

            newState = processMainStageCommand(state, command);
            stateRepository.save(newState);
            return newState;
        }
        return state;
    }

    private State processLobbyStageCommand(State state, Command command) {

        if (isVip(state, command.getPlayerId()) && command.getType().equals("startSesh")) {

            return processStartSeshCommand(state);

        } else if ((isVip(state, command.getPlayerId()) || !hasVip(state)) && command.getType().equals("makeVip")) {

            return processMakeVipCommand(state, command.getPlayerId(), command.getBody());

        } else {

            throw new IllegalArgumentException();
        }
    }

    private State processStartSeshCommand(State state) {

        state.setCurrentQuestionIndex(0L);
        state.setSeshStage(SeshStage.MAIN);
        return state;
    }

    private State processMakeVipCommand(State state, String executerId, String targetId) {

        boolean targetIsValid = state.getPlayers().stream().anyMatch(player -> player.getPlayerId().equals(targetId));
        if (!targetIsValid) {

            return state;
        }
        state.getPlayers().stream().filter(player -> !player.getPlayerId().equals(executerId)).forEach(player -> player.setVip(false));
        state.getPlayers().stream().filter(player -> !player.getPlayerId().equals(targetId)).forEach(player -> player.setVip(true));
        return state;
    }

    private State processMainStageCommand(State state, Command command) {

        String actionType = command.getType();
        switch (actionType) {

            case "nextQuestion" -> processNextQuestionCommand(state, command);
            case "showQuestion" -> processShowQuestionCommand(state, command);
            case "showAnswer" -> processShowAnswerCommand(state, command);
            case "buzzer" -> processBuzzerCommand(state, command);
            default -> log.error("Got a command without a valid Action type. Action={}", command);
        }
        return state;
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
        boolean showQuestion = command.getBody().equals("true");
        state.setShowAnswer(showQuestion);
    }

    private void processBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerId()) && state.getBuzzedPlayerId() != null) return;
        if (!isVip(state, command.getPlayerId())) state.setBuzzedPlayerId(command.getPlayerId());
        if (isVip(state, command.getPlayerId())) state.setBuzzedPlayerId(command.getBody());
    }

    private static boolean hasVip(State state) {

        return state.getPlayers().stream().anyMatch(Player::getVip);
    }

    private static boolean isVip(State state, String playerId) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerName().equals(playerId) && player.getVip());
    }

    private void broadcastState(State state) {

        HostState host = extractHostState(state);
        ControllerState controller = extractControllerState(state);
        SeshUpdate seshUpdate = new SeshUpdate(host, controller);
        broadcastService.broadcastSeshUpdate(seshUpdate, state.getSeshCode());
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

            hostState.setCurrentQuestion(state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue()));
            hostState.setShowQuestion(state.getShowQuestion());
            hostState.setShowAnswer(state.getShowAnswer());
            hostState.setBuzzedPlayerId(state.getBuzzedPlayerId());
        }

        return hostState;
    }

    @Override
    public ControllerState joinAsController(String seshCode, Player player) {

        State state = seshService.getSesh(seshCode);
        boolean seshIsFull = state.getPlayers().size() == state.getMaxPlayer();
        boolean playerAlreadyJoined = playerRepository.existsByState_SeshCodeAndPlayerName(seshCode, player.getPlayerName());
        if (seshIsFull || playerAlreadyJoined) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        player.setState(state);
        player.setVip(false);
        player.setPoints(0L);
        playerRepository.save(player);
        state.getPlayers().add(player);
        stateRepository.save(state);
        return extractControllerState(state);
    }

    @Override
    public HostState joinAsHost(String seshCode, String socketId) {

        State state = seshService.getSesh(seshCode);
        if (state.getHostId() != null) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        state.setHostId(socketId);
        stateRepository.save(state);
        return extractHostState(state);
    }
}
