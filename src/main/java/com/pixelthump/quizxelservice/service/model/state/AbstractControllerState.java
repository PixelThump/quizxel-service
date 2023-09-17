package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AbstractControllerState extends AbstractServiceState {

    private Boolean isVip;
    private String playerName;
    private SeshStage currentStage;
}
