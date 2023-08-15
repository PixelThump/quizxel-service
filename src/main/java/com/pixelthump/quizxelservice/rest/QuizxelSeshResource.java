package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.repository.model.Player;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.rest.model.QuizxelCommand;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.rest.model.state.QuizxelControllerState;
import com.pixelthump.quizxelservice.rest.model.state.QuizxelHostState;
import com.pixelthump.quizxelservice.service.JoinService;
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

        log.info("Started getSesh with seshCode={}", seshCode);
        SeshInfo sesh = seshService.getSeshInfo(seshCode);
        QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
        log.info("Finished getSesh with seshCode={}, result={}", seshCode, quizxelSesh);

        return quizxelSesh;
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
        }catch (Exception e){
            log.warn("Finished hostSesh with seshCode={}, error={}", seshCode, e.toString());
            throw e;
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
        } catch (Exception e) {
            log.warn("Finished addCommand with seshCode={}, command={} error={}", seshCode, quizxelCommand, e.toString());
            throw e;
        }
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public QuizxelControllerState joinAsController(@PathVariable String seshCode, @RequestParam(required = false) String reconnectToken, @RequestBody QuizxelPlayer quizxelPlayer) {

        try {
            log.info("Started joinAsController with seshCode={}, quizxelPlayer={}, reconnectToken={}", seshCode, quizxelPlayer, reconnectToken);
            Player player = modelMapper.map(quizxelPlayer, Player.class);
            ControllerState state = joinService.joinAsController(seshCode, player, reconnectToken);
            QuizxelControllerState controllerState = modelMapper.map(state, QuizxelControllerState.class);
            log.info("Finished joinAsController with seshCode={}, quizxelPlayer={}, reconnectToken={} state={}", seshCode, quizxelPlayer, reconnectToken, controllerState);
            return controllerState;
        } catch (Exception e) {
            log.warn("Finished joinAsController with seshCode={}, quizxelPlayer={}, reconnectToken={}, error={}", seshCode, quizxelPlayer, reconnectToken, e.toString());
            throw e;
        }

    }

    @PostMapping("/{seshCode}/players/host")
    @ResponseBody
    public QuizxelHostState joinAsHost(@PathVariable String seshCode, @RequestParam(required = false) String reconnectToken, @RequestBody QuizxelPlayer quizxelPlayer) {

        log.info("Started joinAsHost with seshCode={}, quizxelPlayer={}, reconnectToken={}", seshCode, quizxelPlayer, reconnectToken);
        try {
            Player player = modelMapper.map(quizxelPlayer, Player.class);
            HostState state = joinService.joinAsHost(seshCode, player.getPlayerId(), reconnectToken);
            QuizxelHostState hostState = modelMapper.map(state, QuizxelHostState.class);
            log.info("Finished joinAsHost with seshCode={}, quizxelPlayer={}, reconnectToken={} state={}", seshCode, quizxelPlayer, reconnectToken, hostState);
            return hostState;
        } catch (Exception e) {
            log.warn("Finished joinAsHost with seshCode={}, quizxelPlayer={}, reconnectToken={}, error={}", seshCode, quizxelPlayer, reconnectToken, e.toString());
            throw e;
        }
    }
}
