package com.pixelthump.quizxelservice.rest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizxelPlayer {

    private QuizxelPlayerId playerId;
    private String playerName;
    private Boolean vip;
    private Long points;
    private QuizxelPlayerIconName playerIconName;

    public QuizxelPlayer(QuizxelPlayerId playerId, boolean vip, long points, QuizxelPlayerIconName playerIconName) {

        this.playerId = playerId;
        this.vip = vip;
        this.points = points;
        this.playerIconName = playerIconName;
    }
}
