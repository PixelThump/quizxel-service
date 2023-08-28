package com.pixelthump.quizxelservice.service.model.state.host;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HostLobbyState extends AbstractHostState {

    private Long maxPlayers;
}
