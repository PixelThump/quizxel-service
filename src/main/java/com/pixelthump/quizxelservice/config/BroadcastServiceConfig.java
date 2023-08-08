package com.pixelthump.quizxelservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.pixelthump.quizxelservice.service.BroadcastService;
import com.pixelthump.quizxelservice.service.StompBroadcastService;

@Configuration
public class BroadcastServiceConfig {

	@Value("${pixelthump.backend-basepath}")
	private String backendBaseUrl;

	@Bean
	@Autowired
	BroadcastService getBroadcastService(StompBroadcastService broadcastService, WebSocketStompClient stompClient) {

		String url = backendBaseUrl.replace("https", "wss://");
		url = url.replace("http", "ws://") + "/ws";
		stompClient.connectAsync(url, broadcastService);
		return broadcastService;
	}
}
