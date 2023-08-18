package com.pixelthump.quizxelservice.service.model.messaging;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import lombok.Data;

@Data
public class MessagingPlayer {

    private String playerId;
    private String playerName;
    private Boolean vip;
    private Long points;
    private PlayerIconName playerIconName;
}
