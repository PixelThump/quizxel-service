package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.rest.model.QuizxelCommandWrapper;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.QuizxelStateWrapper;
import com.pixelthump.quizxelservice.service.GameLogicService;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seshs")
@Log4j2
public class QuizxelSeshResource {
    private final SeshService seshService;
    private final GameLogicService gameLogicService;
    private final ModelMapper modelMapper;

    @Autowired
    public QuizxelSeshResource(SeshService seshService, GameLogicService gameLogicService, ModelMapper modelMapper) {

        this.seshService = seshService;
        this.gameLogicService = gameLogicService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{seshCode}")
    @ResponseBody
    public QuizxelSeshInfo getSeshInfo(@PathVariable String seshCode) {

        log.info("Started getSesh with seshCode={}", seshCode);
        SeshInfo sesh = seshService.getSeshInfo(seshCode);
        QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
        log.info("Finished getSesh with seshCode={}, result={}", seshCode, quizxelSesh);

        return quizxelSesh;
    }

    @PostMapping
    @ResponseBody
    public QuizxelSeshInfo hostSesh(@RequestBody String seshCode){

        log.info("Started hostSesh with seshCode={}", seshCode);
        SeshInfo sesh = seshService.hostSesh(seshCode);
        QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
        log.info("Finished hostSesh with seshCode={}, result={}", seshCode, quizxelSesh);

        return quizxelSesh;
    }

    @PostMapping("/{seshCode}/commands")
    @ResponseBody
    public void addCommand( @PathVariable String seshCode , @RequestBody QuizxelCommandWrapper commandWrapper){
        log.info("Started addCommand with seshCode={}, command={}", seshCode, commandWrapper);
        Command command = modelMapper.map(commandWrapper.getCommand(), Command.class);
        seshService.sendCommandToSesh(command, seshCode);
        log.info("Finished addCommand with seshCode={}, command={}", seshCode, commandWrapper);
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public QuizxelStateWrapper joinAsController(@PathVariable String seshCode , @RequestBody QuizxelPlayer quizxelPlayer){
        log.info("Started joinAsController with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        Map<String, Object> state = gameLogicService.joinAsController(seshCode, player);
        log.info("Finished joinAsController with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);

        return new QuizxelStateWrapper(state);
    }

    @PostMapping("/{seshCode}/players/host")
    @ResponseBody
    public QuizxelStateWrapper joinAsHost(@PathVariable String seshCode , @RequestBody QuizxelPlayer quizxelPlayer){
        log.info("Started joinAsHost with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        Map<String, Object> state = gameLogicService.joinAsHost(seshCode, player.getId());
        log.info("Finished joinAsHost with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);

        return new QuizxelStateWrapper(state);
    }
}
