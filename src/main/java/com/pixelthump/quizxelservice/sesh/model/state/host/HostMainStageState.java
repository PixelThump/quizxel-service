package com.pixelthump.quizxelservice.sesh.model.state.host;
import com.pixelthump.quizxelservice.sesh.model.state.SeshState;
import com.pixelthump.quizxelservice.sesh.model.state.question.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HostMainStageState extends SeshState {

    private Question currentQuestion;
    private boolean showQuestion;
    private boolean showAnswer;
    private String buzzedPlayerId;
}
