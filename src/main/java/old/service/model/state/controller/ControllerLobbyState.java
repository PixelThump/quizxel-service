package com.pixelthump.quizxelservice.service.model.state.controller;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControllerLobbyState extends AbstractControllerState {

    private Boolean hasVip;
    private List<String> questionPackNames;
}
