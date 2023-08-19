package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import com.pixelthump.quizxelservice.service.model.state.controller.AbstractControllerState;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Log4j2
public class JoinServiceImpl implements JoinService {

    private final PlayerRepository playerRepository;
    private final StateRepository stateRepository;
    private final SeshService seshService;
    private final BroadcastService broadcastService;

    @Autowired
    public JoinServiceImpl(PlayerRepository playerRepository, StateRepository stateRepository, SeshService seshService, BroadcastService broadcastService) {

        this.playerRepository = playerRepository;
        this.stateRepository = stateRepository;
        this.seshService = seshService;
        this.broadcastService = broadcastService;
    }

    @Override
    public AbstractControllerState joinAsController(String seshCode, Player player) {

        State state = seshService.getSesh(seshCode);
        boolean seshIsFull = state.getPlayers().size() == state.getMaxPlayer();
        boolean playerAlreadyJoined = playerRepository.existsByPlayerId_PlayerNameAndPlayerId_SeshCode(seshCode, player.getPlayerId().getPlayerName());
        if ((seshIsFull || playerAlreadyJoined)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        player.setState(state);
        player.setVip(false);
        player.setPoints(0L);
        player.setPlayerIconName(PlayerIconName.BASIC);
        playerRepository.save(player);
        state.getPlayers().add(player);
        state.setHasChanged(true);
        stateRepository.save(state);
        return this.broadcastService.getControllerState(player, state);
    }

    @Override
    public AbstractHostState joinAsHost(String seshCode) {

        State state = seshService.getSesh(seshCode);
        if (state.isHostJoined()) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        state.setHostJoined(true);
        state.setHasChanged(true);
        stateRepository.save(state);
        return this.broadcastService.getHostState(state);
    }
}
