package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.QuizxelStateRepository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import com.pixelthump.quizxelservice.repository.model.player.QuizxelPlayerEntity;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.repository.model.question.SimpleBuzzerQuestion;
import com.pixelthump.quizxelservice.service.model.state.*;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class StateServiceImplTest {

    @Autowired
    StateService stateService;
    @MockBean
    QuizxelStateRepository quizxelStateRepository;

    @Test
    void findBySeshCode() {

        stateService.findBySeshCode("abcd");
        verify(quizxelStateRepository).findBySeshCode("abcd");
    }

    @Test
    void existsBySeshCode() {

        stateService.existsBySeshCode("abcd");
        verify(quizxelStateRepository).existsBySeshCode("abcd");
    }

    @Test
    void findBySeshCodeAndActive() {

        stateService.findBySeshCodeAndActive("abcd", true);
        verify(quizxelStateRepository).findBySeshCodeAndActive("abcd", true);
    }

    @Test
    void findByActive() {

        stateService.findByActive(true);
        verify(quizxelStateRepository).findByActive(true);
    }

    @Test
    void save_shouldSaveState(){

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.LOBBY);

        when(quizxelStateRepository.save(state)).thenReturn(state);
        stateService.save(state);
        verify(quizxelStateRepository, times(1)).save(state);
    }

    @Test
    void getHostState_shouldReturnLobbyState() {

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.LOBBY);
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getClass(), HostLobbyState.class);
    }

    @Test
    void getHostState_shouldReturnMainState() {

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.MAIN);
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getClass(), HostMainState.class);
    }

    @Test
    void getControllerState_shouldReturnLobbyState() {

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.LOBBY);

        AbstractServiceState result = stateService.getControllerState(state.getPlayers().get(1), state);
        assertEquals(result.getClass(), ControllerLobbyState.class);
    }
    @Test
    void getControllerState_shouldReturnControllerVipMainState() {

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.MAIN);

        AbstractServiceState result = stateService.getControllerState(state.getPlayers().get(0), state);
        assertEquals(result.getClass(), ControllerVipMainState.class);
    }

    @Test
    void getControllerState_shouldReturnControllerPlayerMainState() {

        QuizxelStateEntity state = getState();
        state.setSeshStage(SeshStage.MAIN);

        AbstractServiceState result = stateService.getControllerState(state.getPlayers().get(1), state);
        assertEquals(result.getClass(), ControllerPlayerMainState.class);
    }

    private QuizxelStateEntity getState(){

        QuizxelStateEntity state = new QuizxelStateEntity();
        state.setShowAnswer(false);
        state.setSeshCode("abcd");
        state.setQuestionpacks(new ArrayList<>());
        state.setShowQuestion(true);
        state.setCurrentQuestionIndex(0L);
        Questionpack selectedQuestionPack = new Questionpack();
        selectedQuestionPack.setPackName("test");
        ArrayList<Question<?>> questions = new ArrayList<>();
        questions.add(new SimpleBuzzerQuestion());
        questions.get(0).setQuestionpack(selectedQuestionPack);
        selectedQuestionPack.setQuestions(questions);
        state.setSelectedQuestionPack(selectedQuestionPack);
        state.setActive(true);
        state.setHasChanged(false);
        state.setHostJoined(true);
        state.setMaxPlayer(5L);
        List<Player> players = new ArrayList<>();
        QuizxelPlayerEntity vip = new QuizxelPlayerEntity();
        vip.setPlayerIconName(PlayerIconName.BASIC);
        vip.setState(state);
        vip.setPlayerId(new PlayerId("vip", state.getSeshCode()));
        vip.setVip(true);
        vip.setPoints(0L);
        players.add(vip);
        QuizxelPlayerEntity player = new QuizxelPlayerEntity();
        player.setPlayerIconName(PlayerIconName.BASIC);
        player.setState(state);
        player.setPlayerId(new PlayerId("player", state.getSeshCode()));
        player.setVip(false);
        player.setPoints(0L);
        players.add(player);
        state.setPlayers(players);
        state.setSeshType("quizxel");
        return state;
    }
}