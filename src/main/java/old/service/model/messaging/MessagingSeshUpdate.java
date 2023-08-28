package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagingSeshUpdate {

    private MessagingSeshStateWrapper host;
    private MessagingSeshStateWrapper controller;
}
