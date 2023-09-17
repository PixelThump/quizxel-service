package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.QuizxelStateRepository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import com.pixelthump.quizxelservice.repository.model.SeshStage;
import com.pixelthump.quizxelservice.repository.model.player.QuizxelPlayerEntity;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.player.MessagingPlayer;
import com.pixelthump.quizxelservice.service.model.question.MessagingQuestion;
import com.pixelthump.quizxelservice.service.model.state.*;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StateServiceImpl implements StateService {

    private final QuizxelStateRepository quizxelStateRepository;

    @Autowired
    public StateServiceImpl(QuizxelStateRepository quizxelStateRepository) {

        this.quizxelStateRepository = quizxelStateRepository;
    }

    @Override
    public State findBySeshCode(String seshCode) {

        return quizxelStateRepository.findBySeshCode(seshCode);
    }

    @Override
    public boolean existsBySeshCode(String seshCode) {

        return quizxelStateRepository.existsBySeshCode(seshCode);
    }

    @Override
    public Optional<? extends State> findBySeshCodeAndActive(String seshCode, Boolean active) {

        return quizxelStateRepository.findBySeshCodeAndActive(seshCode, active);
    }

    @Override
    public List<QuizxelStateEntity> findByActive(Boolean active) {

        return quizxelStateRepository.findByActive(active);
    }

    @Override
    public State save(State state) {

        return quizxelStateRepository.save((QuizxelStateEntity) state);
    }

    @Override
    public AbstractServiceState getHostState(State state) {

        QuizxelStateEntity quizxelState = (QuizxelStateEntity) state;
        AbstractHostState hostState;
        if (quizxelState.getSeshStage().equals(SeshStage.LOBBY)) {
            hostState = getHostLobbyState(quizxelState);
        } else if (quizxelState.getSeshStage().equals(SeshStage.MAIN)) {
            hostState = getHostMainState(quizxelState);
        } else {
            throw new IllegalArgumentException();
        }

        hostState.setPlayers(state.getPlayers().stream().map(this::convertToMessagingPlayer).toList());
        hostState.setCurrentStage(quizxelState.getSeshStage());
        hostState.setSeshCode(state.getSeshCode());
        return hostState;
    }

    @Override
    public AbstractServiceState getControllerState(Player player, State state) {

        QuizxelStateEntity quizxelState = (QuizxelStateEntity) state;
        AbstractControllerState controllerState;
        if (quizxelState.getSeshStage().equals(SeshStage.LOBBY)) {
            controllerState = getControllerLobbyState(player, quizxelState);
        } else if (quizxelState.getSeshStage().equals(SeshStage.MAIN)) {
            controllerState = getControllerMainState(player, quizxelState);
        } else {
            throw new IllegalArgumentException();
        }

        controllerState.setCurrentStage(quizxelState.getSeshStage());
        controllerState.setSeshCode(quizxelState.getSeshCode());
        controllerState.setIsVip(player.getVip());
        controllerState.setPlayerName(player.getPlayerId().getPlayerName());
        return controllerState;
    }

    private AbstractControllerState getControllerMainState(Player player, QuizxelStateEntity state) {

        AbstractControllerMainState mainState;
        if (Boolean.TRUE.equals(player.getVip())) {
            mainState = getControllerVipMainState(state);
        } else {
            mainState = getControllerPlayerMainState(state);
        }
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        return mainState;
    }

    private AbstractControllerMainState getControllerVipMainState(QuizxelStateEntity state) {

        ControllerVipMainState mainState = new ControllerVipMainState();
        mainState.setShowQuestion(state.getShowQuestion());
        mainState.setShowAnswer(state.getShowAnswer());
        Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
        MessagingQuestion<?> currentMessagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        mainState.setCurrentQuestion(currentMessagingQuestion);
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        return mainState;
    }

    private AbstractControllerState getControllerLobbyState(Player player, QuizxelStateEntity state) {

        ControllerLobbyState lobbyState = new ControllerLobbyState();
        lobbyState.setIsVip(player.getVip());
        lobbyState.setHasVip(state.getPlayers().stream().anyMatch(Player::getVip));
        lobbyState.setQuestionPackNames(state.getQuestionpacks().stream().map(Questionpack::getPackName).toList());
        return lobbyState;
    }

    private ControllerPlayerMainState getControllerPlayerMainState(QuizxelStateEntity state) {

        return new ControllerPlayerMainState();
    }

    private MessagingPlayer convertToMessagingPlayer(Player player) {

        QuizxelPlayerEntity quizxelPlayer = (QuizxelPlayerEntity) player;
        MessagingPlayer messagingPlayer = new MessagingPlayer();
        messagingPlayer.setPlayerId(quizxelPlayer.getPlayerId());
        messagingPlayer.setPoints(quizxelPlayer.getPoints());
        messagingPlayer.setVip(quizxelPlayer.getVip());
        messagingPlayer.setPlayerIconName(quizxelPlayer.getPlayerIconName());
        return messagingPlayer;
    }

    private AbstractHostState getHostMainState(QuizxelStateEntity state) {

        HostMainState mainState = new HostMainState();
        mainState.setBuzzedPlayerName(state.getBuzzedPlayerName());
        mainState.setShowAnswer(state.getShowAnswer());
        mainState.setShowQuestion(state.getShowQuestion());
        Question<?> currentQuestion = state.getSelectedQuestionPack().getQuestions().get(state.getCurrentQuestionIndex().intValue());
        MessagingQuestion<?> currentMessagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        mainState.setCurrentQuestion(currentMessagingQuestion);
        return mainState;
    }

    private AbstractHostState getHostLobbyState(State state) {

        HostLobbyState lobbyState = new HostLobbyState();
        lobbyState.setMaxPlayers(state.getMaxPlayer());
        return lobbyState;
    }
}
