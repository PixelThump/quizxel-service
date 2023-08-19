package com.pixelthump.quizxelservice.service.model.state.host;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingQuestion;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class HostMainState extends AbstractHostState {

    private MessagingQuestion<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
    private String buzzedPlayerName;
}
