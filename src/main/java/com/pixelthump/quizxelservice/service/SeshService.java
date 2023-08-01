package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.service.model.SeshInfo;

public interface SeshService {

    SeshInfo getSeshInfo(String seshCode);

    SeshInfo hostSesh(String seshCode);

    void sendCommandToSesh(Command command, String seshCode);

    State getSesh(String seshCode);
}
