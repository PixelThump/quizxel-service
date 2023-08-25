package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingPlayer;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingQuestion;
import com.pixelthump.quizxelservice.service.model.state.AbstractServiceState;
import com.pixelthump.quizxelservice.service.model.state.controller.*;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;
import com.pixelthump.quizxelservice.service.model.state.host.HostLobbyState;
import com.pixelthump.quizxelservice.service.model.state.host.HostMainState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class BroadcastServiceRestImpl implements BroadcastService {

    private final RestTemplate restTemplate;
    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    @Autowired
    public BroadcastServiceRestImpl(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void broadcastSeshUpdate(State state) {

        log.info("Broadcasting with state={}", state);
        String apiUrl = backendBasePath + "/messaging/seshs/" + state.getSeshCode() + "/broadcasts/different";
        Map<String, AbstractServiceState> message = getStateMessage(state);
        log.info("Sending out broadcast={}", message);
        restTemplate.postForEntity(apiUrl, message, String.class);
    }

    private Map<String, AbstractServiceState> getStateMessage(State state) {

        Map<String, AbstractServiceState> stateMessage = getControllerStateMap(state);
        Map.Entry<String, AbstractServiceState> hostStateEntry = getHostStateEntry(state);
        stateMessage.put(hostStateEntry.getKey(), hostStateEntry.getValue());
        return stateMessage;
    }

    private Map.Entry<String, AbstractServiceState> getHostStateEntry(State state) {

        AbstractHostState hostState = getHostState(state);
        return Map.entry("host", hostState);
    }

    public AbstractHostState getHostState(State state) {

        AbstractHostState hostState;
        if (state.getSeshStage().equals(SeshStage.LOBBY)) {
            hostState = getHostLobbyState(state);
        } else {

            hostState = getHostMainState(state);
        }

        hostState.setPlayers(state.getPlayers().stream().map(this::convertToMessagingPlayer).toList());
        hostState.setCurrentStage(state.getSeshStage());
        hostState.setSeshCode(state.getSeshCode());
        return hostState;
    }

    private AbstractHostState getHostMainState(State state) {

        HostMainState mainState = new HostMainState();
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        mainState.setShowAnswer(state.getShowAnswer());
        mainState.setShowQuestion(state.getShowQuestion());
        Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
        MessagingQuestion<?> currentMessagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        mainState.setCurrentQuestion(currentMessagingQuestion);
        return mainState;
    }

    private HostLobbyState getHostLobbyState(State state) {

        HostLobbyState lobbyState = new HostLobbyState();
        lobbyState.setMaxPlayers(state.getMaxPlayer());
        return lobbyState;
    }

    private Map<String, AbstractServiceState> getControllerStateMap(State state) {

        Map<String, AbstractServiceState> map = new HashMap<>();
        for (Player player : state.getPlayers()) {

            AbstractControllerState controllerState = getControllerState(player, state);
            map.put(player.getPlayerId().getPlayerName(), controllerState);
        }
        return map;
    }

    public AbstractControllerState getControllerState(Player player, State state) {

        AbstractControllerState controllerState;
        if (state.getSeshStage().equals(SeshStage.LOBBY)) {

            controllerState = getControllerLobbyState(player, state);
        } else {

            controllerState = getControllerMainState(player, state);
        }

        controllerState.setCurrentStage(state.getSeshStage());
        controllerState.setSeshCode(state.getSeshCode());
        controllerState.setIsVip(player.getVip());
        controllerState.setPlayerName(player.getPlayerId().getPlayerName());
        return controllerState;
    }

    private AbstractControllerMainState getControllerMainState(Player player, State state) {

        AbstractControllerMainState mainState;
        if (Boolean.TRUE.equals(player.getVip())) {
            mainState = getControllerVipMainState(state);
        } else {
            mainState = getControllerPlayerMainState(state);
        }
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        return mainState;
    }

    private ControllerPlayerMainState getControllerPlayerMainState(State state) {

        return new ControllerPlayerMainState();
    }

    private ControllerVipMainState getControllerVipMainState(State state) {

        ControllerVipMainState mainState = new ControllerVipMainState();
        mainState.setShowQuestion(state.getShowQuestion());
        mainState.setShowAnswer(state.getShowAnswer());
        Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
        MessagingQuestion<?> currentMessagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        mainState.setCurrentQuestion(currentMessagingQuestion);
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        return mainState;
    }

    private ControllerLobbyState getControllerLobbyState(Player player, State state) {

        ControllerLobbyState lobbyState = new ControllerLobbyState();
        lobbyState.setIsVip(player.getVip());
        lobbyState.setHasVip(state.getPlayers().stream().anyMatch(Player::getVip));
        lobbyState.setQuestionPackNames(state.getQuestionpacks().stream().map(Questionpack::getPackName).toList());
        return lobbyState;
    }

    private MessagingPlayer convertToMessagingPlayer(Player player) {

        MessagingPlayer messagingPlayer = new MessagingPlayer();
        messagingPlayer.setPlayerId(player.getPlayerId());
        messagingPlayer.setPoints(player.getPoints());
        messagingPlayer.setVip(player.getVip());
        messagingPlayer.setPlayerIconName(player.getPlayerIconName());
        return messagingPlayer;
    }

}
