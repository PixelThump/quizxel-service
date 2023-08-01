package com.pixelthump.quizxelservice.sesh.model;
import com.pixelthump.quizxelservice.sesh.model.state.SeshState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StateWrapper {

    private SeshState state;
}
