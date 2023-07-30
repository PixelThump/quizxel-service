package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.sesh.Sesh;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
class SeshFactoryImplTest {

    @Autowired
    SeshFactory seshfactory;
    String seshCode = "ABCD";;

    @MockBean
    ApplicationContext applicationContext;
    @Autowired
    Sesh sesh;

    @Test
    void CREATE_GAME_WHEN_SUPPORTED_GAME_WITH_SERVICE_SHOULD_RETURN_GAME() {

        when(applicationContext.getBean(Sesh.class)).thenReturn(sesh);
        Sesh result = seshfactory.createSesh(seshCode);
        assertEquals(Sesh.class, result.getClass());
    }

}