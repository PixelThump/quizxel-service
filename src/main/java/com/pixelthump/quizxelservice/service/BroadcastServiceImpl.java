package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.model.SeshUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BroadcastServiceImpl implements BroadcastService{

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pixelthump.backend-basepath}")
    private String backendBasePath;
    @Override
    public void broadcastSeshUpdate(SeshUpdate seshUpdate, String seshCode) {

        String apiUrl = backendBasePath + "/messaging/seshs/" + seshCode + "/broadcasts";
        restTemplate.postForEntity(apiUrl, seshUpdate, String.class);
    }
}
