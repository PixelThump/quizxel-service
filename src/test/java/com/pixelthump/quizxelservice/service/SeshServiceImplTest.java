package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.service.model.Action;
import com.pixelthump.quizxelservice.service.model.Command;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.sesh.Sesh;
import com.pixelthump.quizxelservice.sesh.model.state.SeshState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
class SeshServiceImplTest {


    @Autowired
    SeshService seshService;
    @MockBean
    SeshFactory seshFactory;

    @Test
    void hostSesh_shouldWorkCorrectly() {

        Sesh sesh = Mockito.mock(Sesh.class);
        when(sesh.getSeshCode()).thenReturn("ABCD");
        when(seshFactory.createSesh("ABCD")).thenReturn(sesh);
        SeshInfo result = seshService.hostSesh("ABCD");
        SeshInfo expected = new SeshInfo("quizxel","ABCD");
        assertEquals(expected, result);
    }

    @Test
    void joinAsController(){

        Sesh sesh = Mockito.mock(Sesh.class);
        when(sesh.getSeshCode()).thenReturn("IJKL");
        when(sesh.joinAsHost("IJKL")).thenReturn(null);
        when(seshFactory.createSesh("IJKL")).thenReturn(sesh);
        seshService.hostSesh("IJKL");
        SeshState result = seshService.joinAsHost("IJKL", "ABCD");
        sesh.joinAsHost("ABCD");
        assertNull(result);
    }


    @Test
    void sendCommandToSesh() {
        Sesh sesh = Mockito.mock(Sesh.class);
        when(seshFactory.createSesh("EFGH")).thenReturn(sesh);
        seshService.hostSesh("EFGH");
        seshService.sendCommandToSesh(new Command("EFGH",new Action<>("EFGH","EFGH")), "EFGH");
        verify(sesh).addCommand(any());
    }
}