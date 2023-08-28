package com.pixelthump.quizxelservice.service.model.state;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class HostLobbyState extends AbstractHostState{

    private Long maxPlayers;
}
