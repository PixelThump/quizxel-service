package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.rest.model.QuizxelCommand;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.state.QuizxelControllerState;
import com.pixelthump.quizxelservice.rest.model.state.QuizxelHostState;
import com.pixelthump.quizxelservice.service.GameLogicService;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import com.pixelthump.quizxelservice.service.model.state.ControllerState;
import com.pixelthump.quizxelservice.service.model.state.HostState;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public QuizxelSeshInfo hostSesh(@RequestBody String seshCode) {

        log.info("Started hostSesh with seshCode={}", seshCode);
        SeshInfo sesh = seshService.hostSesh(seshCode);
        QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
        log.info("Finished hostSesh with seshCode={}, result={}", seshCode, quizxelSesh);

        return quizxelSesh;
    }

    @PostMapping("/{seshCode}/commands")
    @ResponseBody
    public void addCommand(@PathVariable String seshCode, @RequestBody QuizxelCommand quizxelCommand) {

        log.info("Started addCommand with seshCode={}, command={}", seshCode, quizxelCommand);
        Command command = modelMapper.map(quizxelCommand, Command.class);
        seshService.sendCommandToSesh(command, seshCode);
        log.info("Finished addCommand with seshCode={}, command={}", seshCode, quizxelCommand);
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public QuizxelControllerState joinAsController(@PathVariable String seshCode, @RequestParam String reconnectToken, @RequestBody QuizxelPlayer quizxelPlayer) {

        log.info("Started joinAsController with seshCode={}, quizxelPlayer={}, reconnectToken={}", seshCode, quizxelPlayer, reconnectToken);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        ControllerState state = gameLogicService.joinAsController(seshCode, player, reconnectToken);
        QuizxelControllerState controllerState = modelMapper.map(state, QuizxelControllerState.class);
        log.info("Finished joinAsController with seshCode={}, quizxelPlayer={}, reconnectToken={} state={}", seshCode, quizxelPlayer, reconnectToken, controllerState);

        return controllerState;
    }

    @PostMapping("/{seshCode}/players/host")
    @ResponseBody
    public QuizxelHostState joinAsHost(@PathVariable String seshCode, @RequestParam String reconnectToken, @RequestBody QuizxelPlayer quizxelPlayer) {

        log.info("Started joinAsHost with seshCode={}, quizxelPlayer={}, reconnectToken={}", seshCode, quizxelPlayer, reconnectToken);
        Player player = modelMapper.map(quizxelPlayer, Player.class);
        HostState state = gameLogicService.joinAsHost(seshCode, player.getPlayerId(), reconnectToken);
        QuizxelHostState hostState = modelMapper.map(state, QuizxelHostState.class);
        log.info("Finished joinAsHost with seshCode={}, quizxelPlayer={}, reconnectToken={} state={}", seshCode, quizxelPlayer, reconnectToken, hostState);

        return hostState;
    }
}
