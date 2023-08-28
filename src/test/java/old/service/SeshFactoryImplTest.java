package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.QuestionPackRepository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest(classes = Application.class)
class SeshFactoryImplTest {

    @Autowired
    SeshFactory seshFactory;
    @MockBean
    QuestionPackRepository questionPackRepository;

    @Test
    void createSesh_shouldCreateSeshStateWithCorrectBaseConfiguration() {

        String seshCode = "ABCD";
        State expected = new State();
        expected.setSeshStage(SeshStage.LOBBY);
        expected.setSeshCode(seshCode);
        expected.setShowAnswer(false);
        expected.setShowQuestion(false);
        expected.setBuzzedPlayerName(null);
        expected.setActive(true);
        expected.setPlayers(new ArrayList<>());
        expected.setCurrentQuestionIndex(0L);
        expected.setHostJoined(false);
        expected.setMaxPlayer(10L);
        Questionpack questionpack = new Questionpack();
        expected.setSelectedQuestionPack(questionpack);
        when(questionPackRepository.findByPackName(any())).thenReturn(Optional.of(questionpack));

        State result = seshFactory.createSesh(seshCode);

        assertEquals(expected, result);
    }
}