package com.pixelthump.quizxelservice.sesh.model.state.controller;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControllerLobbyState extends SeshState {

    private boolean hasVip;
}
