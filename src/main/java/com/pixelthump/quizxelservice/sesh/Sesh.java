package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.service.model.Command;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import com.pixelthump.quizxelservice.sesh.model.SeshUpdate;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;

@Component
@Scope("prototype")
public class Sesh {

    @Getter
    private String seshCode;
    private final StateManager stateManager;
    private final PlayerManager playerManager;
    @Getter
    private LocalDateTime lastInteractionTime;
    @Getter
    private final Deque<Command> unprocessedCommands;
    private final RestTemplate restTemplate;
    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    public Sesh(StateManager stateManager, PlayerManager playerManager, RestTemplate restTemplate) {

        this.stateManager = stateManager;
        this.restTemplate = restTemplate;
        stateManager.setPlayerManager(playerManager);
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

    public SeshState joinAsHost(String socketId){

        if (!playerManager.joinAsHost(socketId)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        this.lastInteractionTime = LocalDateTime.now();

        return getHostState();
    }

    public SeshState joinAsController(String playerName, String socketId) {

        if (this.playerManager.isSeshFull()) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (!this.playerManager.hasHostJoined()) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (!this.playerManager.joinAsPlayer(playerName, socketId)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        this.lastInteractionTime = LocalDateTime.now();

        return this.getControllerState();
    }

    public void addCommand(Command command) {

        if (!this.playerManager.hasPlayerAlreadyJoinedByPlayerId(command.getPlayerId())) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        this.lastInteractionTime = LocalDateTime.now();
        this.unprocessedCommands.offer(command);
    }

    @Scheduled(fixedDelay = 200)
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
        SeshUpdate seshUpdate = new SeshUpdate(getHostState(), getControllerState());
        restTemplate.postForEntity(apiUrl, seshUpdate, String.class);
    }
}
