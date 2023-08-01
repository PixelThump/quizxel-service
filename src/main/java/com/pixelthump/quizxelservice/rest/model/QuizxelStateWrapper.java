package com.pixelthump.quizxelservice.rest.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class QuizxelStateWrapper {

    private Map<String, Object> state;
}
