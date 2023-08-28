package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.service.model.question.MessagingQuestion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class HostMainState extends AbstractHostState{

    private MessagingQuestion<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
    private String buzzedPlayerName;
}
