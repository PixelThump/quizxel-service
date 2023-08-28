package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.QuestionPackRepository;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.seshtypelib.service.StateFactory;
import com.pixelthump.seshtypelib.service.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class StateFactoryImpl implements StateFactory {

    private final QuestionPackRepository questionpackRepository;

    @Autowired
    public StateFactoryImpl(QuestionPackRepository questionpackRepository) {

        this.questionpackRepository = questionpackRepository;
    }

    @Override
    public State createSeshTypeState(String seshCode) {

        QuizxelStateEntity sesh = new QuizxelStateEntity();
        sesh.setSeshStage(SeshStage.LOBBY);
        sesh.setSeshCode(seshCode);
        sesh.setShowAnswer(false);
        sesh.setShowQuestion(false);
        sesh.setBuzzedPlayerName(null);
        sesh.setActive(true);
        sesh.setPlayers(new ArrayList<>());
        sesh.setCurrentQuestionIndex(0L);
        sesh.setHostJoined(false);
        sesh.setMaxPlayer(10L);
        sesh.setSelectedQuestionPack(questionpackRepository.findByPackName("Thomas's Birthday 2023").orElse(null));
        sesh.setQuestionpacks(questionpackRepository.findAll());
        return sesh;
    }
}
