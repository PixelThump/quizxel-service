package com.pixelthump.quizxelservice.rest.model.state;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import lombok.Data;

import java.util.List;

@Data
public class QuizxelControllerState {

    private List<QuizxelPlayer> players;
    private String seshCode;
    private SeshStage currentStage;
    private Long maxPlayers;
    private Boolean hasVip;
    private Question currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
    private String buzzedPlayerId;
}
