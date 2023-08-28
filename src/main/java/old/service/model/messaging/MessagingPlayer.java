package com.pixelthump.quizxelservice.service.model.messaging;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import com.pixelthump.quizxelservice.repository.model.player.PlayerId;
import lombok.Data;

@Data
public class MessagingPlayer {

    private PlayerId playerId;
    private Boolean vip;
    private Long points;
    private PlayerIconName playerIconName;
}
