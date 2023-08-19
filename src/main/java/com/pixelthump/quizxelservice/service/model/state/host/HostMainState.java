package com.pixelthump.quizxelservice.service.model.state.host;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class HostMainState extends AbstractHostState {

    private Question<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
    private String buzzedPlayerName;
}
