package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.verify;

@SpringBootTest(classes = Application.class)
class BroadcastServiceImplTest {

    @Autowired
    BroadcastService broadcastService;
    @MockBean
    private RestTemplate restTemplate;
    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;

    @Test
    void broadcastSeshUpdate() {

        broadcastService.broadcastSeshUpdate(null,"abcd");
        String apiUrl = backendBasePath + "/messaging/seshs/" + "abcd" + "/broadcasts";
        verify(restTemplate).postForEntity(apiUrl, null, String.class);
    }
}