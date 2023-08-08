package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.messaging.*;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class RestBroadcastServiceImpl implements BroadcastService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    @Autowired
    public RestBroadcastServiceImpl(RestTemplate restTemplate, ModelMapper modelMapper) {

        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public void broadcastSeshUpdate(SeshUpdate seshUpdate, String seshCode) {

        log.info("Broadcasting to {} with {}", seshCode, seshCode);

        MessagingSeshStateWrapper host = new MessagingSeshStateWrapper(getHostMap(seshUpdate.getHost()));
        MessagingSeshStateWrapper controller = new MessagingSeshStateWrapper(getControllerMap(seshUpdate.getController()));
        MessagingSeshUpdate messagingSeshUpdate = new MessagingSeshUpdate(host, controller);

        String apiUrl = backendBasePath + "/messaging/seshs/" + seshCode + "/broadcasts/state";
        restTemplate.postForEntity(apiUrl, messagingSeshUpdate, String.class);
    }

    private Map<String, Object> getControllerMap(ControllerState controllerState) {

        Map<String, Object> controller = new HashMap<>();
        // @formatter:off
        List<MessagingPlayer> hostPlayers = modelMapper.map(controllerState.getPlayers(), new TypeToken<List<MessagingPlayer>>() {}.getType());
        // @formatter:on
        Question<?> currentQuestion = controllerState.getCurrentQuestion();
        MessagingQuestion<?> messagingQuestion = null;
        if (currentQuestion != null) {
            messagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        }
        controller.put("players", hostPlayers);
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

    private Map<String, Object> getHostMap(HostState hostState) {

        Map<String, Object> host = new HashMap<>();
        // @formatter:off
        List<MessagingPlayer> hostPlayers = modelMapper.map(hostState.getPlayers(), new TypeToken<List<MessagingPlayer>>() {}.getType());
        // @formatter:on
        Question<?> currentQuestion = hostState.getCurrentQuestion();
        MessagingQuestion<?> messagingQuestion = null;
        if (currentQuestion != null) {
            messagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack().getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
        }
        host.put("players", hostPlayers);
        host.put("seshCode", hostState.getSeshCode());
        host.put("currentStage", hostState.getCurrentStage());
        host.put("maxPlayers", hostState.getMaxPlayers());
        host.put("hasVip", hostState.getHasVip());
        host.put("currentQuestion", messagingQuestion);
        host.put("showQuestion", hostState.getShowQuestion());
        host.put("showAnswer", hostState.getShowAnswer());
        host.put("buzzedPlayerId", hostState.getBuzzedPlayerId());
        return host;
    }

}
