package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.service.model.state.controller.AbstractControllerState;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;

public interface JoinService {

    AbstractControllerState joinAsController(String seshCode, Player player);
    AbstractHostState joinAsHost(String seshCode);
}
