package com.pixelthump.seshtypelib.rest.model.player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeshTypePlayer {

    private SeshTypePlayerId playerId;
    private String playerName;
    private Boolean vip;
    private Long points;
}
