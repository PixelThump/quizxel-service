package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
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
import static org.mockito.Mockito.when;

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
}