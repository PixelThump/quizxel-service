package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.model.SeshUpdate;

public interface BroadcastService {

    void broadcastSeshUpdate(SeshUpdate seshUpdate, String seshCode);
}
