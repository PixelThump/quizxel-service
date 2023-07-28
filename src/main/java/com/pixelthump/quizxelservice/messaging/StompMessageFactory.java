package com.pixelthump.quizxelservice.messaging;
import com.pixelthump.quizxelservice.messaging.model.message.StompMessage;

public interface StompMessageFactory {

    StompMessage getMessage(Object payload) throws UnsupportedOperationException;

    StompMessage getAckMessage();
}
