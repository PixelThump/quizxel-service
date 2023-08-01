package com.pixelthump.quizxelservice.rest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizxelCommand {

    private String playerId;
    private String type;
    private String body;

}
