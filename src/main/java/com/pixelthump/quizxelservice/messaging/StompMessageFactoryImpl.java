package com.pixelthump.quizxelservice.messaging;
import com.pixelthump.quizxelservice.messaging.model.message.StompMessage;
import org.springframework.stereotype.Component;

@Component
public class StompMessageFactoryImpl implements StompMessageFactory {

    @Override
    public StompMessage getMessage(Object payload) throws UnsupportedOperationException {

        return null;
    }

    @Override
    public StompMessage getAckMessage() {

        return null;
    }
}
