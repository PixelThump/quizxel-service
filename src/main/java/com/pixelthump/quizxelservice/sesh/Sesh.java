package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.messaging.MessageBroadcaster;
import com.pixelthump.quizxelservice.messaging.model.Command;
import com.pixelthump.quizxelservice.messaging.model.message.StateStompMessage;
import com.pixelthump.quizxelservice.sesh.exception.PlayerAlreadyJoinedException;
import com.pixelthump.quizxelservice.sesh.exception.PlayerNotInSeshException;
import com.pixelthump.quizxelservice.sesh.exception.SeshCurrentlyNotJoinableException;
import com.pixelthump.quizxelservice.sesh.exception.SeshIsFullException;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
@Scope("prototype")
public class Sesh {

    @Getter
    private String seshCode;
    private final StateManager stateManager;
    private final MessageBroadcaster broadcaster;
    private final PlayerManager playerManager;
    @Getter
    private LocalDateTime lastInteractionTime;
    @Getter
    private final Deque<Command> unprocessedCommands;

    private final RestTemplate restTemplate;
@Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    public Sesh(StateManager stateManager, MessageBroadcaster broadcaster, PlayerManager playerManager, RestTemplate restTemplate) {

        this.stateManager = stateManager;
        this.restTemplate = restTemplate;
        stateManager.setPlayerManager(playerManager);
        this.broadcaster = broadcaster;
        this.playerManager = playerManager;
        unprocessedCommands = new LinkedList<>();
    }

    public void start(String seshCode) {

        if (seshCode == null) {

            throw new IllegalArgumentException();
        }
        stateManager.setSeshCode(seshCode);
        this.seshCode = seshCode;
        this.lastInteractionTime = LocalDateTime.now();
    }

    public SeshState joinAsHost(String socketId) throws PlayerAlreadyJoinedException {

        if (!playerManager.joinAsHost(socketId)) {

            throw new PlayerAlreadyJoinedException("Host has already joined this sesh");
        }
        this.lastInteractionTime = LocalDateTime.now();

        return getHostState();
    }

    public SeshState joinAsController(String playerName, String socketId) throws PlayerAlreadyJoinedException, SeshCurrentlyNotJoinableException, SeshIsFullException {

        if (this.playerManager.isSeshFull()) {

            throw new SeshIsFullException("A maximum of " + PlayerManager.MAX_PLAYERS + " is allowed to join this Sesh.");
        }

        if (!this.playerManager.hasHostJoined()) {

            throw new SeshCurrentlyNotJoinableException("Host hasn't connected yet. Try again later.");
        }

        if (!this.playerManager.joinAsPlayer(playerName, socketId)) {

            throw new PlayerAlreadyJoinedException("Player with name " + playerName + " has already joined the Sesh");
        }

        this.lastInteractionTime = LocalDateTime.now();

        return this.getControllerState();
    }

    public void addCommand(Command command) throws PlayerNotInSeshException {

        if (!this.playerManager.hasPlayerAlreadyJoinedByPlayerId(command.getPlayerId())) {

            throw new PlayerNotInSeshException(command.getPlayerId() + " hasn't joined the sesh.");
        }

        this.lastInteractionTime = LocalDateTime.now();
        this.unprocessedCommands.offer(command);
    }

    @Scheduled(fixedDelay = 33)
    public void processQueue() {

        if (!playerManager.hasHostJoined() && playerManager.getPlayers().isEmpty()) {

            return;
        }

        Deque<Command> queue = new LinkedList<>(this.unprocessedCommands);
        this.unprocessedCommands.clear();

        for (Command command : queue) {

            stateManager.processCommand(command);
        }

        broadcastState();
    }

    private SeshState getHostState() {

        return stateManager.getHostState();
    }

    private SeshState getControllerState() {

        return stateManager.getControllerState();
    }

    private void broadcastState() {

        String apiUrl = backendBasePath + "/messaging/seshs/" + seshCode + "/broadcasts";
        Map<String, StateStompMessage> seshUpdate = new HashMap<>();
        seshUpdate.put("host", new StateStompMessage(getHostState()));
        seshUpdate.put("controller", new StateStompMessage(getControllerState()));
        restTemplate.postForEntity(apiUrl,seshUpdate, String.class);
        broadcastToHost(stateManager.getHostState());
        broadcastToAllControllers(stateManager.getControllerState());
    }

    protected void broadcastToHost(Object payload) {

        broadcaster.broadcastSeshUpdateToHost(seshCode, payload);
    }

    protected void broadcastToAllControllers(Object payload) {

        broadcaster.broadcastSeshUpdateToControllers(seshCode, payload);
    }
}
