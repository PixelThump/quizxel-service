package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class AbstractServiceState {
    private String seshCode;
    private SeshStage currentStage;
}
