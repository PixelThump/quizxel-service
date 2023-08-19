package com.pixelthump.quizxelservice.service.model.state.controller;
import com.pixelthump.quizxelservice.service.model.state.AbstractServiceState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AbstractControllerState extends AbstractServiceState {

    private Boolean isVip;
    private String playerName;
}
