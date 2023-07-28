package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.service.SeshService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
class QuizxelSeshResourceTest {

    @Autowired
    QuizxelSeshResource seshResource;
    @Autowired
    SeshService seshService;
    private final String existingSeshCode = "abcd";
    private final String nonExistingSeshCode = "efgh";

    @BeforeEach
    public void setup() {

        seshService.hostSesh(existingSeshCode);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {

        Field field = seshService.getClass().getDeclaredField("seshs");
        field.setAccessible(true);
        field.set(seshService, new HashMap<>());
    }

    @Test
    void hostSeshInfo_nonexistingSeshCode_shouldReturnCorrectInfoAndHostSesh() {

        QuizxelSeshInfo result = seshResource.hostSesh(nonExistingSeshCode);

        QuizxelSeshInfo expected = new QuizxelSeshInfo();
        expected.setSeshType("QUIZXEL");
        expected.setSeshCode(nonExistingSeshCode);

        assertEquals(expected, result);
    }

    @Test
    void hostSeshInfo_existingSeshCode_shouldThrowCorrectException() {

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> seshResource.hostSesh(existingSeshCode));

        String expectedMessage = "Sesh with seshCode " + existingSeshCode + " already exists";
        System.out.println(result.getMessage());
        assertTrue(result.getMessage().contains(expectedMessage));

        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(409);
        assertEquals(expectedStatusCode, result.getStatusCode());
    }

    @Test
    void getSeshInfo_existingSesh_shouldReturnCorrectInfo() {

        QuizxelSeshInfo result = seshResource.getSeshInfo(existingSeshCode);

        QuizxelSeshInfo expected = new QuizxelSeshInfo();
        expected.setSeshType("QUIZXEL");
        expected.setSeshCode(existingSeshCode);

        assertEquals(expected, result);
    }

    @Test
    void getSeshInfo_nonExistingSesh_shouldThrowCorrectException() {

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> seshResource.getSeshInfo(nonExistingSeshCode));

        String expectedMessage = "Sesh with seshCode " + nonExistingSeshCode + " not found";
        assertTrue(result.getMessage().contains(expectedMessage));

        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(404);
        assertEquals(expectedStatusCode, result.getStatusCode());
    }
}