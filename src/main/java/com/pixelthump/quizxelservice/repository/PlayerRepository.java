package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {

}
