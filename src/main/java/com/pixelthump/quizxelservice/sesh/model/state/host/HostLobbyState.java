package com.pixelthump.quizxelservice.sesh.model.state.host;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HostLobbyState extends SeshState {

    private int maxPlayers;
    private boolean hasVip;
}
