package com.pixelthump.seshtypelib.service;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.repository.model.command.CommandId;
import com.pixelthump.seshtypelib.service.model.SeshInfo;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class SeshServiceImpl implements SeshService {

    private final StateFactory stateFactory;
    private final StateService stateService;
    private final CommandRespository commandRespository;
    private final PlayerService playerService;
    private final GameLogicService gameLogicService;
    private final BroadcastService broadcastService;

    public SeshServiceImpl(StateFactory stateFactory, StateService stateService, CommandRespository commandRespository, PlayerService playerService, GameLogicService gameLogicService, BroadcastService broadcastService) {

        this.stateFactory = stateFactory;
        this.stateService = stateService;
        this.commandRespository = commandRespository;
        this.playerService = playerService;
        this.gameLogicService = gameLogicService;
        this.broadcastService = broadcastService;
    }

    @Override
    public SeshInfo getSeshInfo(String seshCode) {

        State sesh = getSesh(seshCode);
        return extractSeshInfo(sesh);
    }

    @Override
    @Transactional
    public SeshInfo hostSesh(String seshCode) {

        if (stateService.existsBySeshCode(seshCode)) {

            String responseMessage = "Sesh with seshCode " + seshCode + " already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, responseMessage);
        }

        State sesh = createSesh(seshCode);
        stateService.save(sesh);

        return extractSeshInfo(sesh);
    }

    @Override
    @Transactional
    public void sendCommandToSesh(Command command, String seshCode) {

        final State sesh = getSesh(seshCode);
        command.setCommandId(new CommandId(sesh));
        commandRespository.save(command);
    }

    public State getSesh(String seshCode) {

        Optional<State> state = stateService.findBySeshCodeAndActive(seshCode, true);

        if (state.isEmpty()) {

            String responseMessage = "Sesh with seshCode " + seshCode + " not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, responseMessage);
        }

        return state.get();
    }

    @Override
    public AbstractServiceState joinAsController(String seshCode, Player player) {

        State state = getSesh(seshCode);
        boolean seshIsFull = state.getPlayers().size() == state.getMaxPlayer();
        boolean playerAlreadyJoined = playerService.existsByPlayerNameAndSeshCode(seshCode, player.getPlayerId().getPlayerName());
        if ((seshIsFull || playerAlreadyJoined)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        player.setState(state);
        player.setVip(false);
        player.setPoints(0L);
        player = playerService.save(player);
        state.getPlayers().add(player);
        state.setHasChanged(true);
        stateService.save(state);
        return this.stateService.getControllerState(player, state);
    }

    @Override
    public AbstractServiceState joinAsHost(String seshCode) {

        State state = getSesh(seshCode);
        if (state.isHostJoined()) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        state.setHostJoined(true);
        state.setHasChanged(true);
        stateService.save(state);
        return this.stateService.getHostState(state);
    }

    @Override
    public AbstractServiceState getStateForPlayer(String seshCode, String playerName) {

        State state = stateService.findBySeshCode(seshCode);

        AbstractServiceState result;
        if (playerName.equals("host")) {
            result = this.stateService.getHostState(state);
        } else {
            Player player = state.getPlayers().stream().filter(player1 -> player1.getPlayerId().getPlayerName().equals(playerName)).findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            result = this.stateService.getControllerState(player, state);
        }

        return result;
    }

    @Scheduled(fixedDelayString = "${quizxel.tickrate}", initialDelayString = "${quizxel.tickrate}")
    public void processQueues() {

        LocalDateTime startTime = LocalDateTime.now();
        log.debug("starting processQueues at {}", startTime);
        List<State> states = stateService.findByActive(true);
        if (states.isEmpty()) return;
        states.parallelStream().map(gameLogicService::processQueue).map(broadcastService::broadcastSeshUpdate).forEach(stateService::save);
        LocalDateTime endTime = LocalDateTime.now();
        log.debug("Finished processQueues at {} took {} ms", endTime, ChronoUnit.MILLIS.between(startTime, endTime));
    }

    private SeshInfo extractSeshInfo(State sesh) {

        SeshInfo seshInfo = new SeshInfo();
        seshInfo.setSeshCode(sesh.getSeshCode());

        return seshInfo;
    }

    private State createSesh(String seshCode) {

        return stateFactory.createSeshTypeState(seshCode);
    }
}
