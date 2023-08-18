package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public abstract class AbstractServiceState {

    private List<Player> players;
    private String seshCode;
    private SeshStage currentStage;
    private Long maxPlayers;
    private Boolean hasVip;
    private Question<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
    private String buzzedPlayerId;
}
