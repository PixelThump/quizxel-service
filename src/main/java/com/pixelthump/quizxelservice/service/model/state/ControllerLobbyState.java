package com.pixelthump.quizxelservice.service.model.state;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ControllerLobbyState extends AbstractControllerState{

    private Boolean hasVip;
    private List<String> questionPackNames;
}
