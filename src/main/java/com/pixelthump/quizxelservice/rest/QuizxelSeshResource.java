package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.command.QuizxelCommand;
import com.pixelthump.quizxelservice.service.JoinService;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.service.model.state.AbstractServiceState;
import com.pixelthump.quizxelservice.service.model.state.controller.AbstractControllerState;
import com.pixelthump.quizxelservice.service.model.state.host.AbstractHostState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/seshs")
@Log4j2
public class QuizxelSeshResource {

    private final SeshService seshService;
    private final ModelMapper modelMapper;
    private final JoinService joinService;

    @Autowired
    public QuizxelSeshResource(SeshService seshService, ModelMapper modelMapper, JoinService joinService) {

        this.seshService = seshService;
        this.modelMapper = modelMapper;
        this.joinService = joinService;
    }

    @GetMapping("/{seshCode}")
    @ResponseBody
    public QuizxelSeshInfo getSeshInfo(@PathVariable String seshCode) {

        try {
            log.info("Started getSesh with seshCode={}", seshCode);
            SeshInfo sesh = seshService.getSeshInfo(seshCode);
            QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
            log.info("Finished getSesh with seshCode={}, result={}", seshCode, quizxelSesh);
            return quizxelSesh;
        } catch (ResponseStatusException e) {
            log.warn("Finished getSeshInfo with seshCode={}, error={}", seshCode, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished getSeshInfo with seshCode={}, error={}", seshCode, e.toString());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    @PostMapping
    @ResponseBody
    public QuizxelSeshInfo hostSesh(@RequestBody String seshCode) {

        try {
            log.info("Started hostSesh with seshCode={}", seshCode);
            SeshInfo sesh = seshService.hostSesh(seshCode);
            QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
            log.info("Finished hostSesh with seshCode={}, result={}", seshCode, quizxelSesh);
            return quizxelSesh;
        } catch (ResponseStatusException e) {
            log.warn("Finished hostSesh with seshCode={}, error={}", seshCode, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished hostSesh with seshCode={}, error={}", seshCode, e.toString());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    @PostMapping("/{seshCode}/commands")
    @ResponseBody
    public void addCommand(@PathVariable String seshCode, @RequestBody QuizxelCommand quizxelCommand) {

        try {
            log.info("Started addCommand with seshCode={}, command={}", seshCode, quizxelCommand);
            Command command = modelMapper.map(quizxelCommand, Command.class);
            seshService.sendCommandToSesh(command, seshCode);
            log.info("Finished addCommand with seshCode={}, command={}", seshCode, quizxelCommand);
        } catch (ResponseStatusException e) {
            log.warn("Finished addCommand with seshCode={}, command={}, error={}", seshCode, quizxelCommand, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished addCommand with seshCode={}, command={}, error={}", seshCode, quizxelCommand, e.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public AbstractControllerState joinAsController(@PathVariable String seshCode, @RequestBody QuizxelPlayer quizxelPlayer) {

        try {
            log.info("Started joinAsController with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);
            Player player = modelMapper.map(quizxelPlayer, Player.class);
            AbstractControllerState controllerState = joinService.joinAsController(seshCode, player);
            log.info("Finished joinAsController with seshCode={}, quizxelPlayer={}, state={}", seshCode, quizxelPlayer, controllerState);
            return controllerState;
        } catch (ResponseStatusException e) {
            log.warn("Finished joinAsController with seshCode={}, quizxelPlayer={}, error={}", seshCode, quizxelPlayer, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished joinAsController with seshCode={}, quizxelPlayer={}, error={}", seshCode, quizxelPlayer, e.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }

    }

    @PostMapping(value = "/{seshCode}/players/host")
    @ResponseBody
    public AbstractHostState joinAsHost(@PathVariable String seshCode) {

        log.info("Started joinAsHost with seshCode={}", seshCode);
        try {
            AbstractHostState hostState = joinService.joinAsHost(seshCode);
            log.info("Finished joinAsHost with seshCode={}, state={}", seshCode, hostState);
            return hostState;
        } catch (ResponseStatusException e) {
            log.warn("Finished joinAsHost with seshCode={}, error={}", seshCode, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished joinAsHost with seshCode={}, error={}", seshCode, e.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    @GetMapping("/{seshCode}/players/{playerName}/state")
    @ResponseBody
    public AbstractServiceState getPlayerState(@PathVariable String seshCode, @PathVariable String playerName) {

        log.info("Started getPlayerState with seshCode={}, playerName={}", seshCode, playerName);
        try {
            AbstractServiceState state = joinService.getStateForPlayer(seshCode, playerName);
            log.info("Finished getPlayerState with seshCode={}, playerName={}, result={}", seshCode, playerName, state);
            return state;
        } catch (ResponseStatusException e) {
            log.warn("Finished getPlayerState with seshCode={}, playerName={}, error={}", seshCode, playerName, e.toString());
            throw e;
        } catch (Exception e) {
            log.warn("Finished getPlayerState with seshCode={}, playerName={}, error={}", seshCode, playerName, e.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }
}
