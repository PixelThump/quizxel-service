package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingQuestion;
import com.pixelthump.quizxelservice.service.model.messaging.SeshUpdate;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class BroadcastServiceRestImpl implements BroadcastService {

    private final RestTemplate restTemplate;
    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    @Autowired
    public BroadcastServiceRestImpl(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void broadcastSeshUpdate(SeshUpdate seshUpdate, String seshCode) {

        log.info("Broadcasting to {} with {}", seshCode, seshCode);
        String apiUrl = backendBasePath + "/messaging/seshs/" + seshCode + "/broadcasts";
        restTemplate.postForEntity(apiUrl, getControllerMap(seshUpdate.getController()), String.class);
    }

    private Map<String, Object> getControllerMap(ControllerState controllerState) {

        Map<String, Object> controller = new HashMap<>();
        // @formatter:off
        // @formatter:on
        Question<?> currentQuestion = controllerState.getCurrentQuestion();
        MessagingQuestion<?> messagingQuestion = null;
        if (currentQuestion != null) {
            messagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        }
        controller.put("players", controllerState.getPlayers());
        controller.put("seshCode", controllerState.getSeshCode());
        controller.put("currentStage", controllerState.getCurrentStage());
        controller.put("maxPlayers", controllerState.getMaxPlayers());
        controller.put("hasVip", controllerState.getHasVip());
        controller.put("currentQuestion", messagingQuestion);
        controller.put("showQuestion", controllerState.getShowQuestion());
        controller.put("showAnswer", controllerState.getShowAnswer());
        controller.put("buzzedPlayerId", controllerState.getBuzzedPlayerId());
        return controller;
    }
}
