package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.messaging.model.Action;
import com.pixelthump.quizxelservice.messaging.model.Command;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshStage;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import com.pixelthump.quizxelservice.sesh.model.state.controller.ControllerLobbyState;
import com.pixelthump.quizxelservice.sesh.model.state.host.HostLobbyState;
import com.pixelthump.quizxelservice.sesh.model.state.host.HostMainStageState;
import com.pixelthump.quizxelservice.sesh.model.state.question.BuzzerQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class StateManagerTest {

    private static final String SESHCODE = "ABCD";
    @Autowired
    StateManager stateManager;
    @MockBean
    PlayerManager playerManager;
    @MockBean
    QuestionProvider questionProvider;
    List<Player> players;

    @BeforeEach
    void setUp() {

        stateManager.setPlayerManager(playerManager);
        stateManager.setSeshCode(SESHCODE);
        players = new ArrayList<>();
        players.add(new Player("playerName", "playerId"));
    }

    @Test
    void getHostState_shouldReturnHostLobbyState() {

        when(playerManager.getPlayers()).thenReturn(players);

        SeshState result = stateManager.getHostState();

        HostLobbyState expected = new HostLobbyState();
        expected.setPlayers(players);
        expected.setMaxPlayers(5);
        expected.setCurrentStage(SeshStage.LOBBY);
        expected.setSeshCode(SESHCODE);
        expected.setHasVip(false);
        assertEquals(expected, result);
    }

    @Test
    void getControllerState_shouldReturnControllerLobbyState() {

        when(playerManager.getPlayers()).thenReturn(players);

        SeshState result = stateManager.getControllerState();

        ControllerLobbyState expected = new ControllerLobbyState();
        expected.setPlayers(players);
        expected.setCurrentStage(SeshStage.LOBBY);
        expected.setSeshCode(SESHCODE);
        expected.setHasVip(false);
        assertEquals(expected, result);
    }

    @Test
    void processCommand_makeVip_shouldSetVip() {

        when(playerManager.getPlayers()).thenReturn(players);
        when(playerManager.isVIP(any())).thenReturn(false);
        when(playerManager.hasVIP()).thenReturn(false);

        stateManager.processCommand(new Command("playerId", new Action<>("makeVip", "playerId")));

        verify(playerManager).setVIP("playerId");
    }

    @Test
    void processCommand_startSesh_shouldSetCurrentStageToMain() {

        when(playerManager.getPlayers()).thenReturn(players);
        when(playerManager.isVIP(any())).thenReturn(false);
        when(playerManager.hasVIP()).thenReturn(false);
        when(questionProvider.getCurrentQuestion()).thenReturn(new BuzzerQuestion("test", "test"));

        stateManager.processCommand(new Command("playerId", new Action<>("makeVip", "playerId")));

        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("startSesh", "playerId")));

        SeshState result = stateManager.getHostState();
        HostMainStageState expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);
    }

    @Test
    void processCommand_buzzer_shouldHandleCorrectly() {

        when(playerManager.getPlayers()).thenReturn(players);
        when(playerManager.isVIP(any())).thenReturn(false);
        when(playerManager.hasVIP()).thenReturn(false);
        when(questionProvider.getCurrentQuestion()).thenReturn(new BuzzerQuestion("test", "test"));

        stateManager.processCommand(new Command("playerId", new Action<>("makeVip", "playerId")));

        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("startSesh", "playerId")));
        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));

        SeshState result = stateManager.getHostState();
        HostMainStageState expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId("playerId");
        assertEquals(expected, result);

        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));

        result = stateManager.getHostState();
        expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId("playerId");
        assertEquals(expected, result);

        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));

        result = stateManager.getHostState();
        expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);
    }

    @Test
    void processCommand_nextQuestion_shouldHandleCorrectly() {

        when(playerManager.getPlayers()).thenReturn(players);
        when(playerManager.isVIP(any())).thenReturn(false);
        when(playerManager.hasVIP()).thenReturn(false);
        when(questionProvider.getCurrentQuestion()).thenReturn(new BuzzerQuestion("test", "test"));

        stateManager.processCommand(new Command("playerId", new Action<>("makeVip", "playerId")));

        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("startSesh", "playerId")));
        stateManager.processCommand(new Command("playerId", new Action<>("nextQuestion", "next")));
        verify(questionProvider).getNextQuestion();

        stateManager.processCommand(new Command("playerId", new Action<>("nextQuestion", "prev")));
        verify(questionProvider).getPreviousQuestion();

        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("nextQuestion", "next")));
        verify(questionProvider, times(1)).getNextQuestion();
        verify(questionProvider, times(1)).getPreviousQuestion();

        SeshState result = stateManager.getHostState();
        HostMainStageState expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);

        assertEquals(expected, result);
    }

    @Test
    void processCommand_freeBuzzer_shouldHandleCorrectly() {

        when(playerManager.getPlayers()).thenReturn(players);
        when(playerManager.isVIP(any())).thenReturn(false);
        when(playerManager.hasVIP()).thenReturn(false);
        when(questionProvider.getCurrentQuestion()).thenReturn(new BuzzerQuestion("test", "test"));

        stateManager.processCommand(new Command("playerId", new Action<>("makeVip", "playerId")));

        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("startSesh", "playerId")));
        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));
        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("freeBuzzer", null)));


        SeshState result = stateManager.getHostState();
        HostMainStageState expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);

        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));
        when(playerManager.isVIP(any())).thenReturn(true);
        when(playerManager.getPlayer("playerId")).thenReturn(players.get(0));
        stateManager.processCommand(new Command("playerId", new Action<>("freeBuzzer", true)));


        result = stateManager.getHostState();
        expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);

        when(playerManager.isVIP(any())).thenReturn(false);
        stateManager.processCommand(new Command("playerId", new Action<>("buzzer", "playerId")));
        when(playerManager.isVIP(any())).thenReturn(true);
        stateManager.processCommand(new Command("playerId", new Action<>("freeBuzzer", false)));


        result = stateManager.getHostState();
        expected = new HostMainStageState();
        expected.setCurrentStage(SeshStage.MAIN);
        expected.setSeshCode(SESHCODE);
        expected.setPlayers(players);
        expected.setCurrentQuestion(new BuzzerQuestion("test", "test"));
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerId(null);
        assertEquals(expected, result);
    }
}


