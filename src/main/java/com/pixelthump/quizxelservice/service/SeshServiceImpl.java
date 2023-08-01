package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.CommandRespository;
import com.pixelthump.quizxelservice.repository.StateRepository;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.CommandId;
import com.pixelthump.quizxelservice.repository.model.State;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class SeshServiceImpl implements SeshService {

    private final SeshFactory seshFactory;
    private final StateRepository stateRepository;
    private final CommandRespository commandRespository;

    public SeshServiceImpl(SeshFactory seshFactory, StateRepository stateRepository,
                           CommandRespository commandRespository) {

        this.seshFactory = seshFactory;
        this.stateRepository = stateRepository;
        this.commandRespository = commandRespository;
    }

    @Override
    public SeshInfo getSeshInfo(String seshCode) {

        State sesh = getSesh(seshCode);
        return extractSeshInfo(sesh);
    }

    @Override
    public SeshInfo hostSesh(String seshCode) {

        if (stateRepository.existsBySeshCode(seshCode)) {

            String responseMessage = "Sesh with seshCode " + seshCode + " already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, responseMessage);
        }

        State sesh = createSesh(seshCode);
        stateRepository.save(sesh);

        return extractSeshInfo(sesh);
    }

    @Override
    public void sendCommandToSesh(Command command, String seshCode) {

        final State sesh = getSesh(seshCode);
        command.setCommandId(new CommandId(sesh));
        commandRespository.save(command);
    }

    public State getSesh(String seshCode) {

        Optional<State> state = stateRepository.findBySeshCodeAndActive(seshCode, true);

        if (state.isEmpty()) {

            String responseMessage = "Sesh with seshCode " + seshCode + " not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, responseMessage);
        }

        return state.get();
    }

    private SeshInfo extractSeshInfo(State sesh) {

        SeshInfo seshInfo = new SeshInfo();
        seshInfo.setSeshCode(sesh.getSeshCode());

        return seshInfo;
    }

    private State createSesh(String seshCode) {

        return seshFactory.createSesh(seshCode);
    }
}
