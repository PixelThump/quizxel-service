package com.pixelthump.quizxelservice.sesh.model;
import lombok.Data;

@Data
public class Player{

    private String playerName;
    private Boolean vip;
    private String playerId;
    private Long points;

    public Player() {}

    public Player(String playerName, String playerid) {

        this.playerName = playerName;
        this.vip = false;
        this.playerId = playerid;
        this.points = 0L;
    }

    public void addPoints(Long points) {

        this.points += points;
    }

    public boolean getVip() {

        return vip;
    }
}
