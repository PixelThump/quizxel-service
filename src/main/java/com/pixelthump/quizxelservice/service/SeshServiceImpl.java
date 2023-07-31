package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.exception.NoSuchSeshException;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import com.pixelthump.quizxelservice.sesh.Sesh;
import com.pixelthump.quizxelservice.messaging.model.message.CommandStompMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class SeshServiceImpl implements SeshService {

    private final Map<String, Sesh> seshs;
    private final SeshFactory seshFactory;

    public SeshServiceImpl(SeshFactory seshFactory) {

        this.seshs = new HashMap<>();
        this.seshFactory = seshFactory;
    }

    @Override
    public SeshInfo getSeshInfo(String seshCode) {

        Sesh sesh = getSesh(seshCode);
        return extractSeshInfo(sesh);
    }

    @Override
    public SeshInfo hostSesh(String seshCode) {

        if (seshs.containsKey(seshCode)) {

            String responseMessage = "Sesh with seshCode " + seshCode + " already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, responseMessage);
        }

        Sesh sesh = createSesh(seshCode);
        saveSesh(seshCode, sesh);

        return extractSeshInfo(sesh);
    }

    @Override
    public SeshState joinAsController(String seshCode, Player player) {

        final Sesh sesh;

        try {

            sesh = getSesh(seshCode);

        } catch (ResponseStatusException e) {

            throw new NoSuchSeshException(e.getMessage());
        }

        return sesh.joinAsController(player.getPlayerName(), player.getPlayerId());
    }

    public SeshState joinAsHost(String seshCode, String socketId) {

        final Sesh sesh;

        try {

            sesh = getSesh(seshCode);

        } catch (ResponseStatusException e) {

            throw new NoSuchSeshException(e.getMessage());
        }

        return sesh.joinAsHost(socketId);
    }

    @Override
    public void sendCommandToSesh(CommandStompMessage message, String seshCode) throws NoSuchSeshException, UnsupportedOperationException {

        final Sesh sesh;

        try {

            sesh = getSesh(seshCode);

        } catch (ResponseStatusException e) {

            throw new NoSuchSeshException(e.getMessage());
        }

        sesh.addCommand(message.getCommand());
    }

    private Sesh getSesh(String seshCode) {

        Sesh sesh = seshs.get(seshCode);

        if (sesh == null) {

            String responseMessage = "Sesh with seshCode " + seshCode + " not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, responseMessage);
        }

        return sesh;
    }

    private SeshInfo extractSeshInfo(Sesh sesh) {

        SeshInfo seshInfo = new SeshInfo();
        seshInfo.setSeshCode(sesh.getSeshCode());

        return seshInfo;
    }

    private void saveSesh(String seshCode, Sesh sesh) {

        seshs.put(seshCode, sesh);
    }

    private Sesh createSesh(String seshCode) {

        return seshFactory.createSesh(seshCode);
    }
}
