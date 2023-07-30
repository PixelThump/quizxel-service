package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.exception.NoSuchSeshException;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import com.pixelthump.quizxelservice.messaging.model.message.CommandStompMessage;

public interface SeshService {

    SeshInfo getSeshInfo(String seshCode);

    SeshInfo hostSesh(String seshCode);

    SeshState joinAsController(String seshCode, String playerName, String socketId);

    SeshState joinAsHost(String seshCode, String socketId);

    void sendCommandToSesh(CommandStompMessage message, String seshCode) throws NoSuchSeshException, UnsupportedOperationException;
}
