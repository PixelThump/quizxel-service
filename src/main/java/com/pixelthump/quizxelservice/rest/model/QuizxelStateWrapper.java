package com.pixelthump.quizxelservice.rest.model;
import com.pixelthump.quizxelservice.sesh.model.state.SeshState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizxelStateWrapper {

    private SeshState state;
}