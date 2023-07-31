package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.messaging.model.message.CommandStompMessage;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.QuizxelState;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seshs")
@Log4j2
public class QuizxelSeshResource {
    private final SeshService seshService;
    private final ModelMapper modelMapper;

    @Autowired
    public QuizxelSeshResource(SeshService seshService, ModelMapper modelMapper) {

        this.seshService = seshService;
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
    public void addCommand( @PathVariable String seshCode , @RequestBody CommandStompMessage command){
        log.info("Started addCommand with seshCode={}, command={}", seshCode, command);
        seshService.sendCommandToSesh(command, seshCode);
        log.info("Finished addCommand with seshCode={}, command={}", seshCode, command);
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public QuizxelState joinAsController(@PathVariable String seshCode , @RequestBody QuizxelPlayer quizxelPlayer){
        log.info("Started addCommand with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        SeshState state = seshService.joinAsController(seshCode, player);
        log.info("Finished addCommand with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);

        return new QuizxelState(state);
    }

    @PostMapping("/{seshCode}/players/host")
    @ResponseBody
    public QuizxelState joinAsHost( @PathVariable String seshCode , @RequestBody QuizxelPlayer quizxelPlayer){
        log.info("Started addCommand with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        SeshState state = seshService.joinAsHost(seshCode, player.getPlayerId());
        log.info("Finished addCommand with seshCode={}, quizxelPlayer={}", seshCode, quizxelPlayer);

        return new QuizxelState(state);
    }
}
