package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.QuizxelStateRepository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.repository.model.question.SimpleBuzzerQuestion;
import com.pixelthump.quizxelservice.service.model.state.HostLobbyState;
import com.pixelthump.quizxelservice.service.model.state.HostMainState;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void getHostState_shouldReturnLobbyState() {

        QuizxelStateEntity state = new QuizxelStateEntity();
        state.setShowAnswer(false);
        state.setSeshCode("abcd");
        state.setQuestionpacks(new ArrayList<>());
        state.setShowQuestion(true);
        state.setSeshStage(SeshStage.LOBBY);
        state.setCurrentQuestionIndex(0L);
        state.setSelectedQuestionPack(new Questionpack());
        state.setActive(true);
        state.setHasChanged(false);
        state.setHostJoined(true);
        state.setMaxPlayer(5L);
        state.setPlayers(new ArrayList<>());
        state.setSeshType("quizxel");
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getClass(), HostLobbyState.class);
    }

    @Test
    void getHostState_shouldReturnMainState() {

        QuizxelStateEntity state = new QuizxelStateEntity();
        state.setShowAnswer(false);
        state.setSeshCode("abcd");
        state.setQuestionpacks(new ArrayList<>());
        state.setShowQuestion(true);
        state.setSeshStage(SeshStage.MAIN);
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
        state.setPlayers(new ArrayList<>());
        state.setSeshType("quizxel");

        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getClass(), HostMainState.class);
    }
}