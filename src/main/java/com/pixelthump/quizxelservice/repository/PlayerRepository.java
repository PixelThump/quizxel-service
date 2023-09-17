package com.pixelthump.quizxelservice.repository;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, PlayerId> {

    boolean existsByPlayerId_PlayerNameAndPlayerId_SeshCode(String playerName, String seshCode);
}