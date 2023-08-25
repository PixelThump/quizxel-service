package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayerIconName;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayerId;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.command.QuizxelCommand;
import com.pixelthump.quizxelservice.service.GameLogicService;
import com.pixelthump.quizxelservice.service.JoinService;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.service.model.state.controller.AbstractControllerState;
import com.pixelthump.quizxelservice.service.model.state.controller.ControllerPlayerMainState;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;
import com.pixelthump.quizxelservice.service.model.state.host.HostMainState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class QuizxelSeshResourceTest {

    @Autowired
    QuizxelSeshResource seshResource;
    @MockBean
    SeshService seshService;
    private final String existingSeshCode = "abcd";
    private final String nonExistingSeshCode = "efgh";
    private SeshInfo existingSesh;
    private SeshInfo nonExistingSesh;
    @MockBean
    GameLogicService gameLogicService;

    @MockBean
    JoinService joinService;

    @BeforeEach
    public void setup() {

        existingSesh = new SeshInfo();
        existingSesh.setSeshType("QUIZXEL");
        existingSesh.setSeshCode(existingSeshCode);

        nonExistingSesh = new SeshInfo();
        nonExistingSesh.setSeshType("QUIZXEL");
        nonExistingSesh.setSeshCode(nonExistingSeshCode);
    }

    @Test
    void getSeshInfo_existingSesh_shouldReturnCorrectInfo() {

        when(seshService.getSeshInfo(any())).thenReturn(existingSesh);

        QuizxelSeshInfo result = seshResource.getSeshInfo(existingSeshCode);

        QuizxelSeshInfo expected = new QuizxelSeshInfo();
        expected.setSeshType("QUIZXEL");
        expected.setSeshCode(existingSeshCode);

        assertEquals(expected, result);
    }

    @Test
    void getSeshInfo_nonExistingSesh_shouldThrowCorrectException() {

        String responseMessage = "Sesh with seshCode " + nonExistingSeshCode + " not found";
        when(seshService.getSeshInfo(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, responseMessage));
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> seshResource.getSeshInfo(nonExistingSeshCode));

        assertTrue(result.getMessage().contains(responseMessage));

        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(404);
        assertEquals(expectedStatusCode, result.getStatusCode());
    }

    @Test
    void hostSeshInfo_nonexistingSeshCode_shouldReturnCorrectInfoAndHostSesh() {

        when(seshService.hostSesh(any())).thenReturn(nonExistingSesh);

        QuizxelSeshInfo result = seshResource.hostSesh(nonExistingSeshCode);

        QuizxelSeshInfo expected = new QuizxelSeshInfo();
        expected.setSeshType("QUIZXEL");
        expected.setSeshCode(nonExistingSeshCode);

        assertEquals(expected, result);
    }

    @Test
    void hostSeshInfo_existingSeshCode_shouldThrowCorrectException() {

        String responseMessage = "Sesh with seshCode " + existingSeshCode + " already exists";
        when(seshService.hostSesh(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, responseMessage));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> seshResource.hostSesh(existingSeshCode));

        System.out.println(result.getMessage());
        assertTrue(result.getMessage().contains(responseMessage));

        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(409);
        assertEquals(expectedStatusCode, result.getStatusCode());
    }

    @Test
    void addCommand_existingSesh_shouldCallSeshService() {

        QuizxelCommand command = new QuizxelCommand("abcd", "buzzer", "abcd");
        seshResource.addCommand(existingSeshCode, command);
        verify(seshService, times(1)).sendCommandToSesh(any(), eq(existingSeshCode));
    }

    @Test
    void joinAsController_existingSesh_shouldCallGameLogicServiceAndReturnState() {

        AbstractControllerState state = new ControllerPlayerMainState();
        when(joinService.joinAsController(eq(existingSeshCode), any())).thenReturn(state);
        AbstractControllerState result = seshResource.joinAsController(existingSeshCode, new QuizxelPlayer(new QuizxelPlayerId("abcd", existingSeshCode), false, 0L, QuizxelPlayerIconName.BASIC));
        AbstractControllerState expected = new ControllerPlayerMainState();
        assertEquals(expected, result);
    }

    @Test
    void joinAsHost_existingSesh_shouldCallGameLogicServiceAndReturnState() {

        AbstractHostState state = new HostMainState();
        when(joinService.joinAsHost(existingSeshCode)).thenReturn(state);
        AbstractHostState result = seshResource.joinAsHost(existingSeshCode);
        AbstractHostState expected = new HostMainState();
        assertEquals(expected, result);
    }
}
