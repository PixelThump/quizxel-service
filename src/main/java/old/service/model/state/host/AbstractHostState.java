package com.pixelthump.quizxelservice.service.model.state.host;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingPlayer;
import com.pixelthump.quizxelservice.service.model.state.AbstractServiceState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AbstractHostState extends AbstractServiceState {

    private List<MessagingPlayer> players;
}
