package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.seshtypelib.service.PlayerService;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    @Override
    public boolean existsByPlayerNameAndSeshCode(String playerName, String seshCode) {

        return playerRepository.existsByPlayerId_PlayerNameAndPlayerId_SeshCode(playerName, seshCode);
    }

    @Override
    public Player save(Player player) {

        return playerRepository.save(player);
    }
}
