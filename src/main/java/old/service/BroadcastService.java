package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.service.model.state.controller.AbstractControllerState;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;

public interface BroadcastService {

    void broadcastSeshUpdate(State state);

     AbstractHostState getHostState(State state);
     AbstractControllerState getControllerState(Player player, State state);
}
