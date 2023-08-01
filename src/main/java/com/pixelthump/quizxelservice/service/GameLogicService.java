package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.Player;

import java.util.Map;

public interface GameLogicService {

    Map<String, Object> joinAsController(String seshCode, Player player);


    Map<String, Object> joinAsHost(String seshCode, String socketId);
    void processQueues();
}
