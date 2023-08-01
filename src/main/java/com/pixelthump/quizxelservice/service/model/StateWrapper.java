package com.pixelthump.quizxelservice.service.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class StateWrapper {

    private Map<String, Object> state;
}
