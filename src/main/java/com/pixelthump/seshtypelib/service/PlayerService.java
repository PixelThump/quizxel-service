package com.pixelthump.seshtypelib.service;
import com.pixelthump.seshtypelib.service.model.player.Player;

public interface PlayerService {

    boolean existsByPlayerNameAndSeshCode(String playerName, String seshCode);

    Player save(Player player);
}
