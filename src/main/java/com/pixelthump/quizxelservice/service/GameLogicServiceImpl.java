package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.service.model.SeshUpdate;
import com.pixelthump.quizxelservice.service.model.StateWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class GameLogicServiceImpl implements GameLogicService {

    private final StateRepository stateRepository;
    private final CommandRespository commandRespository;

    private final BroadcastService broadcastService;
    private final SeshService seshService;

    @Autowired
    public GameLogicServiceImpl(StateRepository stateRepository, CommandRespository commandRespository, BroadcastService broadcastService, SeshService seshService) {

        this.stateRepository = stateRepository;
        this.commandRespository = commandRespository;
        this.broadcastService = broadcastService;
        this.seshService = seshService;
    }

    @Scheduled(fixedDelay = 100)
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
            } catch (Exception e) {

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

        if (isVip(state, command.getPlayerid()) && command.getType().equals("startSesh")) {

            return processStartSeshCommand(state);

        } else if ((isVip(state, command.getPlayerid()) || !hasVip(state)) && command.getType().equals("makeVip")) {

            return processMakeVipCommand(state, command.getPlayerid(), command.getBody());

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

        boolean targetIsValid = state.getPlayers().stream().anyMatch(player -> player.getId().equals(targetId));
        if (!targetIsValid) {

            return state;
        }
        state.getPlayers().stream().filter(player -> !player.getId().equals(executerId)).forEach(player -> player.setVip(false));
        state.getPlayers().stream().filter(player -> !player.getId().equals(targetId)).forEach(player -> player.setVip(true));
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

        if (!isVip(state, command.getPlayerid())) return;
        if ("next".equals(command.getBody())) state.nextQuestion();
        if ("prev".equals(command.getBody())) state.prevQuestion();

        state.setShowQuestion(false);
        state.setShowAnswer(false);
    }

    private void processShowQuestionCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerid())) return;
       boolean showQuestion = command.getBody().equals("true");
        state.setShowQuestion(showQuestion);
    }

    private void processShowAnswerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerid())) return;
        boolean showQuestion = command.getBody().equals("true");
        state.setShowAnswer(showQuestion);
    }

    private void processBuzzerCommand(State state, Command command) {

        if (!isVip(state, command.getPlayerid()) && state.getBuzzedPlayerId() != null) return;
        if (!isVip(state, command.getPlayerid())) state.setBuzzedPlayerId(command.getPlayerid());
        if (isVip(state, command.getPlayerid())) state.setBuzzedPlayerId(command.getBody());
    }

    private static boolean hasVip(State state) {

        return state.getPlayers().stream().anyMatch(Player::getVip);
    }

    private static boolean isVip(State state, String playerId) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerName().equals(playerId) && player.getVip());
    }

    private void broadcastState(State state) {

        StateWrapper host = extractHostState(state);
        StateWrapper controller = extractControllerState(state);
        SeshUpdate seshUpdate = new SeshUpdate(host, controller);
        broadcastService.broadcastSeshUpdate(seshUpdate, state.getSeshCode());
    }

    private StateWrapper extractControllerState(State state) {

        return extractHostState(state);
    }

    private StateWrapper extractHostState(State state) {

        Map<String, Object> hostState = new HashMap<>();
        hostState.put("players", state.getPlayers());
        hostState.put("seshCode", state.getSeshCode());
        hostState.put("currentStage", state.getSeshStage());

        if (state.getSeshStage() == SeshStage.LOBBY) {

            hostState.put("maxPlayers", state.getMaxPlayer());
            hostState.put("hasVip", hasVip(state));

        } else if (state.getSeshStage() == SeshStage.MAIN) {

            hostState.put("currentQuestion", state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue()));
            hostState.put("showQuestion", state.getShowQuestion());
            hostState.put("showAnswer", state.getShowAnswer());
            hostState.put("buzzedPlayerId", state.getBuzzedPlayerId());
        }

        return new StateWrapper(hostState);
    }

    @Override
    public Map<String, Object> joinAsController(String seshCode, Player player) {

        State state = seshService.getSesh(seshCode);
        if (state.getPlayers().size()== state.getMaxPlayer()){

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        state.getPlayers().add(player);
        return extractControllerState(state).getState();
    }

    @Override
    public Map<String, Object> joinAsHost(String seshCode, String socketId) {

        State state = seshService.getSesh(seshCode);
        if (state.getHostId()!= null){

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        state.setHostId(socketId);
        return extractHostState(state).getState();
    }
}
