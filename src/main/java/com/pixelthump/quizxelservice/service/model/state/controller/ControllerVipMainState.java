package com.pixelthump.quizxelservice.service.model.state.controller;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingQuestion;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControllerVipMainState extends AbstractControllerMainState {

    private MessagingQuestion<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;

}
