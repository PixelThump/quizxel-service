package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.Data;

@Data
public class MessagingPlayer {

    private String playerId;
    private String playerName;
    private Boolean vip;
    private Long points;
}
