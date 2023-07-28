package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
class QuizxelSeshResourceTest {

    @Autowired
    QuizxelSeshResource seshResource;
    private final String existingSeshCode = "abcd";
    private final String nonExistingSeshCode = "efgh";

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