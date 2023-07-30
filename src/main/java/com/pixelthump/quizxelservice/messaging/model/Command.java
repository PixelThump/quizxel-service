package com.pixelthump.quizxelservice.messaging.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {

    private String playerId;
    private Action<?> action;
}
