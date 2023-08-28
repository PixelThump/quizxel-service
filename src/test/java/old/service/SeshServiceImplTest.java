package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
class SeshServiceImplTest {

    @Autowired
    SeshService seshService;
    @MockBean
    SeshFactory seshFactory;
    @MockBean
    StateRepository stateRepository;
    @MockBean
    CommandRespository commandRespository;

    @Test
    void getSeshInfo_shouldReturnCorrectInfomation(){
        String seshCode = "abcd";
        when(stateRepository.findBySeshCodeAndActive(seshCode, true)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, ()-> seshService.getSeshInfo(seshCode));
    }

    @Test
    void getSeshInfo_shouldThrowException(){
        String seshCode = "abcd";
        State state = new State();
        state.setSeshCode(seshCode);
        when(stateRepository.findBySeshCodeAndActive(seshCode, true)).thenReturn(Optional.of(state));
        SeshInfo result =  seshService.getSeshInfo(seshCode);
        SeshInfo expected = new SeshInfo("quizxel", seshCode);
        assertEquals(expected, result);
    }

    @Test
    void hostSesh_shouldSaveState(){
        String seshCode = "abcd";
        when(stateRepository.existsBySeshCode(seshCode)).thenReturn(false);
        State state = new State();
        state.setSeshCode(seshCode);
        when(seshFactory.createSesh(seshCode)).thenReturn(state);
        SeshInfo result = seshService.hostSesh(seshCode);
        SeshInfo expected = new SeshInfo("quizxel", seshCode);
        assertEquals(expected, result);
        verify(seshFactory).createSesh(seshCode);
        verify(stateRepository).save(any());
        verify(stateRepository).existsBySeshCode(seshCode);
    }

    @Test
    void hostSesh_sehscCdeexists_shouldThrowExcpetion(){
        String seshCode = "abcd";
        when(stateRepository.existsBySeshCode(seshCode)).thenReturn(true);
        assertThrows(ResponseStatusException.class, ()->seshService.hostSesh(seshCode));
    }

    @Test
    void sendCommandToSesh(){

        String seshCode = "abcd";
        State state = new State();
        state.setSeshCode(seshCode);
        when(stateRepository.findBySeshCodeAndActive(seshCode, true)).thenReturn(Optional.of(state));
        Command command = new Command();
        seshService.sendCommandToSesh(command, seshCode);
        verify(commandRespository).save(command);
        assertTrue(command.getCommandId().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(command.getCommandId().getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}