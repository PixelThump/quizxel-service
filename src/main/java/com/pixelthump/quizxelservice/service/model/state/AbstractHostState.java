package com.pixelthump.quizxelservice.service.model.state;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.service.model.player.MessagingPlayer;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AbstractHostState extends AbstractServiceState {

    private List<MessagingPlayer> players;
    private SeshStage currentStage;
}
