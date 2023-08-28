package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagingSeshStateWrapper {

    private Map<String, Object> state;
}
