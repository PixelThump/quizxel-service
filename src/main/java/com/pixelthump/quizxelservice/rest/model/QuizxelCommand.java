package com.pixelthump.quizxelservice.rest.model;
import lombok.Data;

@Data
public class QuizxelCommand {

    private String playerId;
    private String type;
    private String body;
}
