package com.pixelthump.quizxelservice.service.model.player;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MessagingPlayer {

    private PlayerId playerId;
    private Boolean vip;
    private Long points;
    private PlayerIconName playerIconName;
}
