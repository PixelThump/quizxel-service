package com.pixelthump.quizxelservice.rest.model;
import lombok.Data;

@Data
public class QuizxelPlayer {

    private String id;
    private String playerName;
    private Boolean vip;
    private Long points;

}
