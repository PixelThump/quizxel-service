package com.pixelthump.quizxelservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import com.pixelthump.quizxelservice.repository.model.question.Question;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingPlayer;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingQuestion;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingSeshStateWrapper;
import com.pixelthump.quizxelservice.service.model.messaging.MessagingSeshUpdate;
import com.pixelthump.quizxelservice.service.model.messaging.SeshUpdate;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StompBroadcastService extends StompSessionHandlerAdapter{

	private StompSession session;
	private final ModelMapper modelMapper;

	public StompBroadcastService(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	@Override
	public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {

		this.session = session;
	}

	public void broadcastSeshUpdate(SeshUpdate seshUpdate, String seshCode) throws NullPointerException {

		if (session == null) {
			throw new NullPointerException();
		}

		log.info("Broadcasting to {} with {}", seshCode, seshUpdate);

		MessagingSeshStateWrapper host = new MessagingSeshStateWrapper(getHostMap(seshUpdate.getHost()));
		MessagingSeshStateWrapper controller = new MessagingSeshStateWrapper(getControllerMap(seshUpdate.getController()));
		MessagingSeshUpdate messagingSeshUpdate = new MessagingSeshUpdate(host, controller);

		String topic = "/topic/seshs/" + seshCode + "/broadcasts/state";
		session.send(topic, messagingSeshUpdate);
	}

	private Map<String, Object> getControllerMap(ControllerState controllerState) {

		Map<String, Object> controller = new HashMap<>();
		// @formatter:off
		List<MessagingPlayer> hostPlayers = modelMapper.map(controllerState.getPlayers(), new TypeToken<List<MessagingPlayer>>() {}.getType());
		// @formatter:on
		Question<?> currentQuestion = controllerState.getCurrentQuestion();
		MessagingQuestion<?> messagingQuestion = null;
		if (currentQuestion != null) {
			messagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack()
				.getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
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
			messagingQuestion = new MessagingQuestion<>(currentQuestion.getQuestionpack()
				.getPackName(), currentQuestion.getText(), currentQuestion.getType(), currentQuestion.getAnswer());
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
