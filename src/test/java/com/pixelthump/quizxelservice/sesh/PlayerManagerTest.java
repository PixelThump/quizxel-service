package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.Application;
import com.pixelthump.quizxelservice.sesh.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
class PlayerManagerTest {

    PlayerManager playerManager;
    String playerId;
    String playerName;
    String hostId;

    @BeforeEach
    void setUp(@Autowired PlayerManager playerManager) {

        this.playerManager = playerManager;
        playerId = "playerId";
        playerName = "player";
        hostId = "hostId";
    }

    @Test
    void addPlayerToSesh_should_add_player_to_players() {

        this.playerManager.joinAsPlayer(playerName, playerId);

        List<Player> playersList = playerManager.getPlayers();
        assertEquals(playerName, playersList.get(0).getPlayerName());
        assertEquals(playerId, playersList.get(0).getPlayerId());
    }

    @Test
    void joinASHost_shouldSetHostIdAndReturnTrue() {

        boolean result = this.playerManager.joinAsHost(hostId);
        assertTrue(result);
        String resultId = playerManager.getHostId();
        assertEquals(hostId, resultId);
    }

    @Test
    void joinASHost_hostAlreadyJoined_shouldReturnFalse() {

        this.playerManager.joinAsHost(hostId);
        boolean result = this.playerManager.joinAsHost(hostId);
        assertFalse(result);
    }

    @Test
    void setVip_hasVip_shouldSetVipIdAndReturnTrue() {

        playerManager.joinAsPlayer(playerName, playerId);
        playerManager.setVIP(playerId);
        assertTrue(playerManager.hasVIP());
        assertTrue(playerManager.isVIP(playerId));
    }

    @Test
    void getPlayer_shouldReturnCorrectPlayer(){

        playerManager.joinAsPlayer(playerName, playerId);
        Player result = playerManager.getPlayer(playerId);
        Player expected = new Player(playerName, playerId);
        assertEquals(expected, result);
        assertEquals(1, playerManager.getPlayerCount());
    }
}
