package com.pixelthump.quizxelservice.rest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizxelPlayer {

    private String playerId;
    private String playerName;
    private Boolean vip;
    private Long points;
    private QuizxelPlayerIconName playerIconName;
}
