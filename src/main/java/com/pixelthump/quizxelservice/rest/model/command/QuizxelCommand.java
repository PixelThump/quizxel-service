package com.pixelthump.quizxelservice.rest.model.command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizxelCommand {

    private String playerName;
    private String type;
    private String body;

}
