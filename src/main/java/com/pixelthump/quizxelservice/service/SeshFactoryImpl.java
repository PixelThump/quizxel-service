package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.QuestionPackRepository;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SeshFactoryImpl implements SeshFactory{

    private final QuestionPackRepository questionPackRepository;

    @Autowired
    public SeshFactoryImpl(QuestionPackRepository questionPackRepository) {

        this.questionPackRepository = questionPackRepository;
    }

    public State createSesh(String seshCode){

        State sesh = new State();
        sesh.setSeshStage(SeshStage.LOBBY);
        sesh.setSeshCode(seshCode);
        sesh.setShowAnswer(false);
        sesh.setShowQuestion(false);
        sesh.setBuzzedPlayerId(null);
        sesh.setActive(true);
        sesh.setPlayers(new ArrayList<>());
        sesh.setCurrentQuestionIndex(0L);
        sesh.setHostId(null);
        sesh.setMaxPlayer(10L);
        sesh.setSelectedQuestionPack(questionPackRepository.findByPackName("Thomas's Birthday 2023"));
        return sesh;
    }
}
