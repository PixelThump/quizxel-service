package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.service.model.Action;
import com.pixelthump.quizxelservice.service.model.Command;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshStage;
import com.pixelthump.quizxelservice.sesh.model.state.SeshState;
import com.pixelthump.quizxelservice.sesh.model.state.controller.ControllerLobbyState;
import com.pixelthump.quizxelservice.sesh.model.state.host.HostLobbyState;
import com.pixelthump.quizxelservice.sesh.model.state.host.HostMainStageState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Log4j2
public class StateManager {

    @Setter
    PlayerManager playerManager;
    QuestionProvider questionProvider;
    @Getter
    @Setter
    private String seshCode;
    protected SeshStage currentStage;
    private String buzzedPlayerId;
    private boolean showQuestion;
    private boolean showAnswer;

    @Autowired
    public StateManager(QuestionProvider questionProvider) {

        this.questionProvider = questionProvider;
        currentStage = SeshStage.LOBBY;
    }

    SeshState getHostState() {
//        TODO: Implement;

        if (currentStage == SeshStage.LOBBY) {

            HostLobbyState state = new HostLobbyState();
            state.setMaxPlayers(PlayerManager.MAX_PLAYERS);
            state.setSeshCode(seshCode);
            state.setPlayers(playerManager.getPlayers());
            state.setCurrentStage(currentStage);
            state.setHasVip(playerManager.hasVIP());
            return state;

        } else if (currentStage == SeshStage.MAIN) {

            HostMainStageState state = new HostMainStageState();
            state.setPlayers(playerManager.getPlayers());
            state.setSeshCode(getSeshCode());
            state.setCurrentStage(currentStage);
            state.setCurrentQuestion(questionProvider.getCurrentQuestion());
            state.setBuzzedPlayerId(buzzedPlayerId);
            state.setShowQuestion(showQuestion);
            state.setShowAnswer(showAnswer);

            return state;
        }
        return null;
    }

    public SeshState getControllerState() {

        //        TODO: Implement;
        if (currentStage == SeshStage.LOBBY) {

            ControllerLobbyState state = new ControllerLobbyState();
            state.setSeshCode(seshCode);
            state.setPlayers(playerManager.getPlayers());
            state.setCurrentStage(currentStage);
            state.setHasVip(playerManager.hasVIP());

            return state;

        } else if (currentStage == SeshStage.MAIN) {

            return getHostState();
        }
        return null;
    }

    public void processCommand(Command command) {

        if (currentStage == SeshStage.LOBBY) {

            processLobbyCommand(command);
        } else if (currentStage == SeshStage.MAIN) {

            processMainCommand(command);
        }
    }

    private void startMainStage() {

        this.currentStage = SeshStage.MAIN;
        questionProvider.getFirstQuestion();
    }

    private void processLobbyCommand(Command command) {

        String playerId = command.getPlayerId();
        Action<?> action = command.getAction();

        if (this.playerManager.isVIP(playerId) && action.getType().equals("startSesh")) {

            this.startMainStage();

        } else if ((this.playerManager.isVIP(playerId) || !this.playerManager.hasVIP()) && action.getType().equals("makeVip")) {

            this.playerManager.setVIP((String) action.getBody());
        }
    }

    private void processMainCommand(Command command) {

        Action<?> action = command.getAction();
        String actionType = action.getType();

        switch (actionType) {

            case "nextQuestion" -> handleNextQuestionCommand(command);
            case "showQuestion" -> handleShowQuestionCommand(command);
            case "showAnswer" -> handleShowAnswerCommand(command);
            case "buzzer" -> handleBuzzerCommand(command);
            case "freeBuzzer" -> handleFreeBuzzerCommand(command);
            default -> log.error("QuizxelSesh: Got a command without a valid Action type. Action={}", action);
        }
    }

    private void handleShowAnswerCommand(Command command) {

        Action<Boolean> action = (Action<Boolean>) command.getAction();
        String executerId = command.getPlayerId();

        if (!this.playerManager.isVIP(executerId)) return;

        this.showAnswer = action.getBody();
    }

    private void handleShowQuestionCommand(Command command) {

        Action<Boolean> action = (Action<Boolean>) command.getAction();
        String executerId = command.getPlayerId();

        if (!this.playerManager.isVIP(executerId)) return;

        this.showQuestion = action.getBody();
    }

    private void handleBuzzerCommand(Command command) {

        String executerId = command.getPlayerId();

        if (isBuzzed() && !this.playerManager.isVIP(executerId)) return;

        if (this.playerManager.isVIP(executerId)) {

            unlockBuzzer();

        } else {

            buzzer(executerId);
        }
    }

    private void handleFreeBuzzerCommand(Command command) {

        Action<Boolean> action = (Action<Boolean>) command.getAction();
        String executerId = command.getPlayerId();

        if (!this.playerManager.isVIP(executerId)) return;
        if (!isBuzzed()) return;

        if (action.getBody() == null) {

            unlockBuzzer();

        } else if (Boolean.TRUE.equals(action.getBody())) {

            awardPointsToPlayer(this.buzzedPlayerId);
            unlockBuzzer();

        } else if (Boolean.FALSE.equals(action.getBody())) {

            awardPointsToAllOtherPlayers(this.buzzedPlayerId);
            unlockBuzzer();
        }
    }

    private void handleNextQuestionCommand(Command command) {

        if (!this.playerManager.isVIP(command.getPlayerId())) return;
        Action<String> action = (Action<String>) command.getAction();

        if ("next".equals(action.getBody())) questionProvider.getNextQuestion();
        if ("prev".equals(action.getBody())) questionProvider.getPreviousQuestion();

        this.showQuestion = false;
        this.showAnswer = false;
    }

    private void awardPointsToPlayer(String buzzedPlayerId) {

        this.playerManager.getPlayer(buzzedPlayerId).addPoints(this.playerManager.getPlayerCount() - 2);
    }

    private void awardPointsToAllOtherPlayers(String buzzedPlayerId) {

        for (Player player : this.playerManager.getPlayers()) {

            if (!player.getPlayerId().equals(buzzedPlayerId)) {

                player.addPoints(1L);
            }
        }
    }

    private boolean isBuzzed() {

        return this.buzzedPlayerId != null;
    }

    private void unlockBuzzer() {

        this.buzzedPlayerId = null;
    }

    private void buzzer(String playerId) {

        this.buzzedPlayerId = playerId;
    }

}
