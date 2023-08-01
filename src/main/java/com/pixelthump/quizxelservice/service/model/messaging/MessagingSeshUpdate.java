package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagingSeshUpdate {

    private Map<String, Object> host;
    private Map<String, Object> controller;
}
