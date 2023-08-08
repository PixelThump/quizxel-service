package com.pixelthump.quizxelservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.pixelthump.quizxelservice.service.BroadcastService;
import com.pixelthump.quizxelservice.service.StompBroadcastService;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class BroadcastServiceConfig {

	@Value("${pixelthump.backend-basepath}")
	private String backendBaseUrl;

	@Autowired
	BroadcastService StompBroadcastService(StompBroadcastService broadcastService, WebSocketStompClient stompClient) {

		String url = backendBaseUrl.replace("https", "wss://");
		url = url.replace("http", "ws://") + "/ws";
		log.error(url);
		stompClient.connectAsync(url, broadcastService);
		return broadcastService;
	}
}
