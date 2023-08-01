package com.pixelthump.quizxelservice.service.model.messaging;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeshUpdate {

    private HostState host;
    private ControllerState controller;

}
