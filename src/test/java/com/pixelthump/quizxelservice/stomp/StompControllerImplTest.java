package com.pixelthump.quizxelservice.stomp;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.messaging.MessageBroadcaster;
import com.pixelthump.quizxelservice.messaging.StompMessageFactory;
import com.pixelthump.quizxelservice.messaging.model.Action;
import com.pixelthump.quizxelservice.messaging.model.Command;
import com.pixelthump.quizxelservice.messaging.model.message.*;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.exception.NoSuchSeshException;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class StompControllerImplTest {

    @MockBean
    SeshService seshServiceMock;
    @MockBean
    MessageBroadcaster broadcasterMock;
    @MockBean
    StompMessageFactory factoryMock;
    @Autowired
    SeshStompController seshStompcontroller;
    String seshCode;
    String playerName;
    String socketId;

    @BeforeEach
    void setUp() {

        seshCode = "abcd";
        playerName = "roboter5123";
        socketId = "asfd7465asd";
    }

    @Test
    void joinSessionAsHost_should_return_error_message_when_called_with_non_existent_session() {

        NoSuchSeshException exception = new NoSuchSeshException("No session with code " + seshCode + " exists");
        when(seshServiceMock.joinAsHost(seshCode, socketId)).thenThrow(exception);

        ErrorStompMessage expected = new ErrorStompMessage(exception.getMessage());
        when(factoryMock.getMessage(exception)).thenReturn(expected);

        StompMessage result = seshStompcontroller.joinSeshAsHost(seshCode, socketId);

        assertEquals(expected, result);
    }

    @Test
    void joinSessionAsHost_should_return_state_message_when_called_with_existing_session() {

        SeshState state = new SeshState();
        when(seshServiceMock.joinAsHost(seshCode, socketId)).thenReturn(state);

        StompMessage expected = new StateStompMessage(state);
        when(factoryMock.getMessage(any())).thenReturn(expected);

        StompMessage result = seshStompcontroller.joinSeshAsHost(seshCode, socketId);

        assertEquals(expected, result);
    }

    @Test
    void joinSessionAsController_should_return_error_message_when_called_with_non_existent_session() {

        NoSuchSeshException exception = new NoSuchSeshException("No session with code " + seshCode + " exists");
        when(seshServiceMock.joinAsController(seshCode, new Player(playerName, socketId))).thenThrow(exception);

        ErrorStompMessage expected = new ErrorStompMessage(exception.getMessage());
        when(factoryMock.getMessage(exception)).thenReturn(expected);

        StompMessage result = seshStompcontroller.joinSeshAsController(playerName, seshCode, socketId);

        assertEquals(expected, result);
    }

    @Test
    void joinSessionAsController_should_return_state_message_when_called_with_existing_session() {

        SeshState state = new SeshState();
        when(seshServiceMock.joinAsController(seshCode, new Player(playerName, socketId))).thenReturn(state);

        StompMessage expected = new StateStompMessage(state);
        when(factoryMock.getMessage(any())).thenReturn(expected);

        StompMessage result = seshStompcontroller.joinSeshAsController(playerName, seshCode, socketId);

        assertEquals(expected, result);
    }

    @Test
    void sendCommandToGame_Should_not_thow_exception_and_call_game_add_command() {

        StompMessage expected = new GenericStompMessage();

        when(factoryMock.getAckMessage()).thenReturn(expected);

        Command incomingCommand = new Command(socketId, new Action<>(playerName, "Chat message"));
        CommandStompMessage incomingMessage = new CommandStompMessage(incomingCommand);
        StompMessage result = seshStompcontroller.sendCommandToSesh(incomingMessage, seshCode, this.socketId);
        assertEquals(expected, result);
        verify(seshServiceMock).sendCommandToSesh(incomingMessage, seshCode);
    }

    @Test
    void sendCommandToGame_with_nonexistant_seshcode_Should_Return_ErrorMessage() {

        NoSuchSeshException exception = new NoSuchSeshException("No Session with code " + seshCode + " exists");
        doThrow(exception).when(seshServiceMock).sendCommandToSesh(any(), any());
        when(factoryMock.getMessage(exception)).thenReturn(new ErrorStompMessage(exception.getMessage()));

        StompMessage expected = new ErrorStompMessage(exception.getMessage());

        Command incomingCommand = new Command(socketId, new Action<>(playerName, "Chat message"));
        CommandStompMessage incomingMessage = new CommandStompMessage(incomingCommand);
        StompMessage result = seshStompcontroller.sendCommandToSesh(incomingMessage, seshCode, this.socketId);
        assertEquals(expected, result);
    }
}
