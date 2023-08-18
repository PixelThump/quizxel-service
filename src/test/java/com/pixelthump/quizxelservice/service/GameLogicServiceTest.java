package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.command.CommandId;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.command.Command;
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
    StateRepository stateRepository;
    @MockBean
    CommandRespository commandRespository;
    @MockBean
    BroadcastServiceStompImpl BroadcastServiceStompImpl;
    @MockBean
    SeshService seshService;

    @Test
    void processQueues_shouldGetAllQueues(){

        when(stateRepository.findByActive(true)).thenReturn(new ArrayList<>());
        gameLogicService.processQueues();
        verify(stateRepository, times(1)).findByActive(true);
    }

    @Test
    void processQueues_shouldProcessQueue(){

        List<State> seshs = new ArrayList<>();
        String seshCode = "ABCD";
        State state = new State();
        state.setSeshCode(seshCode);
        state.setHostId(seshCode);
        state.getPlayers().add(new Player());
        seshs.add(state);
        when(stateRepository.findByActive(true)).thenReturn(seshs);
        when(stateRepository.findBySeshCode(any())).thenReturn(seshs.get(0));
        List<Command> commands = getAllCommands(state);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(seshCode)).thenReturn(commands);
        gameLogicService.processQueues();
        verify(stateRepository, times(1)).findByActive(true);
    }

    private List<Command> getAllCommands(State state) {
        List<Command> commands = new ArrayList<>();
        Command makeVipCommand = new Command();
        makeVipCommand.setCommandId(new CommandId(state));
        makeVipCommand.setBody("ABCD");
        return commands;
    }
}