package com.pixelthump.quizxelservice.messaging;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.messaging.model.message.GenericStompMessage;
import com.pixelthump.quizxelservice.messaging.model.message.StompMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
class MessageBroadcasterImplTest {

    @MockBean
    StompMessageFactory factory;
    @MockBean
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    MessageBroadcaster messageBroadcaster;
    String sessionCode;


    @BeforeEach
    void setUp() {
        sessionCode = "abcd";
    }

    @Test
    void broadcastSeshUpdateToControllers_supportedPayload_shouldCallConvertAndSendWithCorrectDestination() {

        StompMessage stompMessage = new GenericStompMessage();
        when(factory.getMessage(factory)).thenReturn(stompMessage);
        messageBroadcaster.broadcastSeshUpdateToControllers(sessionCode, factory);
        verify(messagingTemplate).convertAndSend("/topic/sesh/" + sessionCode + "/controller", stompMessage);
    }
    @Test
    void broadcastSeshUpdateToHost_supportedPayload_shouldCallConvertAndSendWithCorrectDestination() {

        StompMessage stompMessage = new GenericStompMessage();
        when(factory.getMessage(factory)).thenReturn(stompMessage);
        messageBroadcaster.broadcastSeshUpdateToHost(sessionCode, factory);
        verify(messagingTemplate).convertAndSend("/topic/sesh/" + sessionCode + "/host", stompMessage);
    }

    @Test
    void broadcastSeshUpdateToControllers_WITH_NON_SUPPORTED_PAYLOAD_SHOULD_THROW_EXCEPTION() {

        when(factory.getMessage(factory)).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> messageBroadcaster.broadcastSeshUpdateToControllers(sessionCode, factory));
    }

    @Test
    void broadcastSeshUpdateToHost_WITH_NON_SUPPORTED_PAYLOAD_SHOULD_THROW_EXCEPTION() {

        when(factory.getMessage(factory)).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> messageBroadcaster.broadcastSeshUpdateToHost(sessionCode, factory));
    }
}