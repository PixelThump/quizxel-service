package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.seshtypelib.service.PlayerService;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = Application.class)
class PlayerServiceImplTest {

    @Autowired
    PlayerService playerService;
    @MockBean
    PlayerRepository playerRepository;


    @Test
    void existsByPlayerNameAndSeshCode() {

        playerService.existsByPlayerNameAndSeshCode("abcd", "efgh");
        verify(playerRepository).existsByPlayerId_PlayerNameAndPlayerId_SeshCode("abcd", "efgh");
    }

    @Test
    void save() {

        playerService.save(new Player());
        verify(playerRepository).save(new Player());
    }
}