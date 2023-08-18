package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.PlayerRepository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.player.PlayerIconName;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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

    @Autowired
    public JoinServiceImpl(PlayerRepository playerRepository, StateRepository stateRepository, SeshService seshService) {

        this.playerRepository = playerRepository;
        this.stateRepository = stateRepository;
        this.seshService = seshService;
    }

    @Override
    public ControllerState joinAsController(String seshCode, Player player) {
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
        return extractControllerState(state);
    }

    @Override
    public HostState joinAsHost(String seshCode) {

        State state = seshService.getSesh(seshCode);
        String hostId = state.getHostId();
        if (hostId != null) {

            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        state.setHasChanged(true);
        stateRepository.save(state);
        return extractHostState(state);
    }


    private ControllerState extractControllerState(State state) {

        HostState hostState = extractHostState(state);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(hostState, ControllerState.class);
    }

    private HostState extractHostState(State state) {

        HostState hostState = new HostState();
        hostState.setPlayers(state.getPlayers());
        hostState.setSeshCode(state.getSeshCode());
        hostState.setCurrentStage(state.getSeshStage());

        if (state.getSeshStage() == SeshStage.LOBBY) {

            hostState.setMaxPlayers(state.getMaxPlayer());
            hostState.setHasVip(hasVip(state));

        } else if (state.getSeshStage() == SeshStage.MAIN) {

            Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
            hostState.setCurrentQuestion(currentQuestion);
            hostState.setShowQuestion(state.getShowQuestion());
            hostState.setShowAnswer(state.getShowAnswer());
            hostState.setBuzzedPlayerId(state.getBuzzedPlayerName());
        }

        return hostState;
    }

    private static boolean hasVip(State state) {

        return state.getPlayers().stream().anyMatch(Player::getVip);
    }
}
