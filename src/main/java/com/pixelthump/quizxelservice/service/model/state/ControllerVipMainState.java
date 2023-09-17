package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.service.model.question.MessagingQuestion;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ControllerVipMainState extends AbstractControllerMainState {

    private MessagingQuestion<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;
}
