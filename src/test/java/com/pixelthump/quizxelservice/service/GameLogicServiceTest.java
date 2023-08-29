package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.QuestionPackRepository;
import com.pixelthump.quizxelservice.repository.QuizxelStateRepository;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import com.pixelthump.quizxelservice.repository.model.player.QuizxelPlayerEntity;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.repository.model.command.CommandId;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = "quizxel.tickrate=99999999")
class GameLogicServiceTest {

    @Autowired
    GameLogicService gameLogicService;
    @MockBean
    QuizxelStateRepository stateRepository;
    @MockBean
    CommandRespository commandRespository;
    @MockBean
    QuestionPackRepository questionPackRepository;

    @Test
    void processQueue_shouldProcessQueue() {

        List<QuizxelStateEntity> seshs = new ArrayList<>();
        String seshCode = "ABCD";
        QuizxelStateEntity state = new QuizxelStateEntity();
        state.setSeshCode(seshCode);
        state.setHostJoined(true);
        state.getPlayers().add(new QuizxelPlayerEntity());
        seshs.add(state);
        when(stateRepository.findByActive(true)).thenReturn(seshs);
        when(stateRepository.findBySeshCode(any())).thenReturn(seshs.get(0));
        List<Command> commands = getAllCommands(state);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(seshCode)).thenReturn(commands);
        gameLogicService.processQueue(seshCode);
        verify(stateRepository, times(1)).findBySeshCode(seshCode);
        verify(stateRepository, times(1)).save(any());
    }

    private List<Command> getAllCommands(State state) {

        List<Command> commands = new ArrayList<>();
        Command makeVipCommand = new Command();
        makeVipCommand.setCommandId(new CommandId(state));
        makeVipCommand.setBody("ABCD");
        return commands;
    }
}