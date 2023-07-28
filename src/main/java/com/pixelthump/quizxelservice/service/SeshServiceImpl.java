package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.model.Sesh;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class SeshServiceImpl implements SeshService {

    private final Map<String, Sesh> seshs;

    public SeshServiceImpl() {

        this.seshs = new HashMap<>();
    }

    @Override
    public SeshInfo getSeshInfo(String seshCode) {

        Sesh sesh = seshs.get(seshCode);

        if (sesh == null){

            String responseMessage = "Sesh with seshCode " + seshCode + " not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, responseMessage);
        }

        return extractSeshInfo(sesh);
    }

    private SeshInfo extractSeshInfo(Sesh sesh) {

        SeshInfo seshInfo = new SeshInfo();
        seshInfo.setSeshCode(sesh.getSeshCode());
        return seshInfo;
    }
}
