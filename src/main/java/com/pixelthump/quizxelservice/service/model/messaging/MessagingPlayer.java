package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.Data;

@Data
public class MessagingPlayer {

    private String id;
    private String playerName;
    private Boolean vip;
    private Long points;
}
