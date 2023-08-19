package com.pixelthump.quizxelservice.service.model.state.controller;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AbstractControllerMainState extends AbstractControllerState {

    private String buzzedPlayerName;
}
