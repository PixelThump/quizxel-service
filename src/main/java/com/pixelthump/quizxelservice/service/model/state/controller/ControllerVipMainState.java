package com.pixelthump.quizxelservice.service.model.state.controller;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControllerVipMainState extends AbstractControllerMainState {

    private Question<?> currentQuestion;
    private Boolean showQuestion;
    private Boolean showAnswer;

}
