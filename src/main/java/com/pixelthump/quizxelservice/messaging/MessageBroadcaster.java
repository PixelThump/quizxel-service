package com.pixelthump.quizxelservice.messaging;
public interface MessageBroadcaster {

    void broadcastSeshUpdateToControllers(String seshcode, Object payload);

    void broadcastSeshUpdateToHost(String seshcode, Object payload);
}
