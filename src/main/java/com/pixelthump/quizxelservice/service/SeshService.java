package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.model.Command;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshState;

public interface SeshService {

    SeshInfo getSeshInfo(String seshCode);

    SeshInfo hostSesh(String seshCode);

    SeshState joinAsController(String seshCode, Player player);

    SeshState joinAsHost(String seshCode, String socketId);

    void sendCommandToSesh(Command message, String seshCode);
}
