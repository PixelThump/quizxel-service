package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.messaging.MessageBroadcaster;
import com.pixelthump.quizxelservice.messaging.model.Action;
import com.pixelthump.quizxelservice.messaging.model.Command;
import com.pixelthump.quizxelservice.sesh.exception.PlayerAlreadyJoinedException;
import com.pixelthump.quizxelservice.sesh.exception.PlayerNotInSeshException;
import com.pixelthump.quizxelservice.sesh.exception.SeshCurrentlyNotJoinableException;
import com.pixelthump.quizxelservice.sesh.exception.SeshIsFullException;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshStage;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import com.pixelthump.quizxelservice.sesh.model.state.controller.ControllerLobbyState;
import com.pixelthump.quizxelservice.sesh.model.state.host.HostLobbyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class QuizxelSeshTest {

    private static final String SESH_CODE = "ABCD";
    @Autowired
    Sesh sesh;
    @MockBean
    MessageBroadcaster broadcaster;
    @MockBean
    StateManager stateManager;
    @MockBean
    PlayerManager playerManager;
    @MockBean
    RestTemplate restTemplate;
    String hostSocketId = "hostId";
    HostLobbyState hostLobbyState;
    String playerId = "playerId";
    String playerName = "player";
    ControllerLobbyState playerLobbyState;
    Command command;

    @BeforeEach
    void setup() {

        hostLobbyState = new HostLobbyState();
        hostLobbyState.setCurrentStage(SeshStage.LOBBY);
        hostLobbyState.setSeshCode(SESH_CODE);
        hostLobbyState.setPlayers(new ArrayList<>());
        hostLobbyState.setMaxPlayers(5);
        hostLobbyState.setHasVip(false);

        playerLobbyState = new ControllerLobbyState();
        playerLobbyState.setCurrentStage(SeshStage.LOBBY);
        playerLobbyState.setSeshCode(SESH_CODE);
        playerLobbyState.setPlayers(new ArrayList<>());
        playerLobbyState.setHasVip(false);

        command = new Command(playerId, new Action<>("type", "body"));
    }

    @Test
    void start_shouldSetSeshCodeAndLastInteractionTime() {

        sesh.start(SESH_CODE);
        assertTrue(lastInteractionTimeIsAboutNow());
        assertEquals(SESH_CODE, sesh.getSeshCode());
    }

    @Test
    void start_null_shouldThrowNullPointerException() {

        assertThrows(IllegalArgumentException.class, () -> sesh.start(null));
    }

    @Test
    void joinAsHost_hostAlreadyJoined_shouldThrowPlayerAlreadyJoinedexception() {

        when(playerManager.joinAsHost(hostSocketId)).thenReturn(false);
        assertThrows(PlayerAlreadyJoinedException.class, () -> sesh.joinAsHost(hostSocketId));
    }

    @Test
    void joinAsHost_shouldReturnHostState() {

        when(playerManager.joinAsHost(hostSocketId)).thenReturn(true);
        when(stateManager.getHostState()).thenReturn(hostLobbyState);
        SeshState result = sesh.joinAsHost(hostSocketId);
        assertEquals(hostLobbyState, result);
    }

    @Test
    void joinAsController_fullSesh_shouldThrowSeshIsFullException() {

        when(playerManager.isSeshFull()).thenReturn(true);
        assertThrows(SeshIsFullException.class, () -> sesh.joinAsController(playerName, playerId));
    }

    @Test
    void joinAsController_hostHasNotJoined_shouldThrowSeshCurrentlyNotJoinableException() {

        when(playerManager.isSeshFull()).thenReturn(false);
        when(this.playerManager.hasHostJoined()).thenReturn(false);
        assertThrows(SeshCurrentlyNotJoinableException.class, () -> sesh.joinAsController(playerName, playerId));
    }

    @Test
    void joinAsController_playerHasAlreadyJoined_shouldThrowPlayerAlreadyJoinedException() {

        when(playerManager.isSeshFull()).thenReturn(false);
        when(playerManager.hasHostJoined()).thenReturn(true);
        when(playerManager.joinAsPlayer(playerName, playerId)).thenReturn(false);
        assertThrows(PlayerAlreadyJoinedException.class, () -> sesh.joinAsController(playerName, playerId));
    }

    @Test
    void joinAsController_playerHasAlreadyJoined_shouldReturnStateAndSetLastInteractionTime() {

        when(playerManager.isSeshFull()).thenReturn(false);
        when(playerManager.hasHostJoined()).thenReturn(true);
        when(playerManager.joinAsPlayer(playerName, playerId)).thenReturn(true);
        when(stateManager.getControllerState()).thenReturn(playerLobbyState);
        SeshState result = sesh.joinAsController(playerName, playerId);
        assertTrue(lastInteractionTimeIsAboutNow());
        assertEquals(playerLobbyState, result);
    }

    @Test
    void addCommand_playerHasNotJoined_shouldThrowPlayerNotInSeshException() {

        when(playerManager.hasPlayerAlreadyJoinedByPlayerId(playerId)).thenReturn(false);
        assertThrows(PlayerNotInSeshException.class, () -> sesh.addCommand(command));
    }

    @Test
    void addCommand_shouldAddCommandAndSetlastInteractionTime() {

        when(playerManager.hasPlayerAlreadyJoinedByPlayerId(playerId)).thenReturn(true);
        sesh.addCommand(command);
        assertTrue(lastInteractionTimeIsAboutNow());
        assertTrue(sesh.getUnprocessedCommands().contains(command));
    }

    @Test
    void processQueue_noPlayersHaveJoined_shouldNotcallAnything() {

        Deque<Command> unprocessedCommands = sesh.getUnprocessedCommands();
        unprocessedCommands.add(command);
        when(playerManager.getPlayers()).thenReturn(new ArrayList<>());
        when(playerManager.hasHostJoined()).thenReturn(false);
        sesh.processQueue();
        assertTrue(unprocessedCommands.contains(command));
        verify(stateManager, never()).processCommand(any());
        unprocessedCommands.remove();
    }

    @Test
    void processQueue_shouldClearCommandsAndProcessCommandsAndBroadcastState() {

        sesh.start(SESH_CODE);
        Deque<Command> unprocessedCommands = sesh.getUnprocessedCommands();
        unprocessedCommands.add(command);
        List<Player> playerList = new ArrayList<>();
        playerList.add(new Player(playerName, playerId));
        when(playerManager.getPlayers()).thenReturn(playerList);
        when(playerManager.hasHostJoined()).thenReturn(true);
        when(stateManager.getControllerState()).thenReturn(playerLobbyState);
        when(stateManager.getHostState()).thenReturn(hostLobbyState);
        when(restTemplate.postForEntity(any(), any(), eq(String.class))).thenReturn(ResponseEntity.of(Optional.of("")));
        sesh.processQueue();

        assertTrue(unprocessedCommands.isEmpty());
        verify(stateManager).processCommand(command);
        verify(broadcaster).broadcastSeshUpdateToHost(SESH_CODE, hostLobbyState);
        verify(broadcaster).broadcastSeshUpdateToControllers(SESH_CODE, playerLobbyState);
    }

    boolean lastInteractionTimeIsAboutNow() {

        return sesh.getLastInteractionTime().isAfter(LocalDateTime.now().minusSeconds(1)) && sesh.getLastInteractionTime().isBefore(LocalDateTime.now().plusSeconds(1));
    }
}