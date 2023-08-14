package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;

public interface GameLogicService {

    ControllerState joinAsController(String seshCode, Player player, String reconnectToken);

    HostState joinAsHost(String seshCode, String socketId, String reconnectToken);

    void processQueues();
}
