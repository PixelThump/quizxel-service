package com.pixelthump.quizxelservice.rest.model;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizxelState {

    private SeshState state;
}
