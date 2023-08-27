package com.pixelthump.seshtypelib.rest;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.seshtypelib.rest.model.command.SeshTypeCommand;
import com.pixelthump.seshtypelib.rest.model.player.SeshTypePlayer;
import com.pixelthump.seshtypelib.rest.model.seshinfo.SeshTypeSeshInfo;
import com.pixelthump.seshtypelib.service.SeshService;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.SeshInfo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/seshs")
@Log4j2
public class SeshResource {

    private final SeshService seshService;
    private final ModelMapper modelMapper;

    public SeshResource(SeshService seshService, ModelMapper modelMapper) {

        this.seshService = seshService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{seshCode}")
    @ResponseBody
    public SeshTypeSeshInfo getSeshInfo(@PathVariable String seshCode) {

        log.info("Started getSeshInfo with seshCode={}", seshCode);
        SeshInfo seshInfo = seshService.getSeshInfo(seshCode);
        SeshTypeSeshInfo seshTypeSeshInfo = modelMapper.map(seshInfo, SeshTypeSeshInfo.class);
        log.info("Finished getSeshInfo with seshCode={}, result={}", seshCode, seshTypeSeshInfo);

        return seshTypeSeshInfo;
    }

    @PostMapping
    @ResponseBody
    public SeshTypeSeshInfo hostSesh(@RequestBody String seshCode) {

        try {
            log.info("Started hostSesh with seshCode={}", seshCode);
            SeshInfo sesh = seshService.hostSesh(seshCode);
            SeshTypeSeshInfo seshTypeSeshInfo = modelMapper.map(sesh, SeshTypeSeshInfo.class);
            log.info("Finished hostSesh with seshCode={}, result={}", seshCode, seshTypeSeshInfo);
            return seshTypeSeshInfo;
        } catch (Exception e) {
            log.warn("Finished hostSesh with seshCode={}, error={}", seshCode, e.toString());
            throw e;
        }
    }

    @PostMapping("/{seshCode}/commands")
    @ResponseBody
    public void addCommand(@PathVariable String seshCode, @RequestBody SeshTypeCommand seshTypeCommand) {

        try {
            log.info("Started addCommand with seshCode={}, command={}", seshCode, seshTypeCommand);
            Command command = modelMapper.map(seshTypeCommand, Command.class);
            seshService.sendCommandToSesh(command, seshCode);
            log.info("Finished addCommand with seshCode={}, command={}", seshCode, seshTypeCommand);
        } catch (Exception e) {
            log.warn("Finished addCommand with seshCode={}, command={} error={}", seshCode, seshTypeCommand, e.toString());
            throw e;
        }
    }

    @PostMapping("/{seshCode}/players/controller")
    @ResponseBody
    public AbstractServiceState joinAsController(@PathVariable String seshCode, @RequestBody SeshTypePlayer seshTypePlayer) {

        try {
            log.info("Started joinAsController with seshCode={}, quizxelPlayer={}", seshCode, seshTypePlayer);
            Player player = modelMapper.map(seshTypePlayer, Player.class);
            AbstractServiceState controllerState = seshService.joinAsController(seshCode, player);
            log.info("Finished joinAsController with seshCode={}, quizxelPlayer={}, state={}", seshCode, seshTypePlayer, controllerState);
            return controllerState;
        } catch (Exception e) {
            log.warn("Finished joinAsController with seshCode={}, quizxelPlayer={}, error={}", seshCode, seshTypePlayer, e.toString());
            throw e;
        }
    }

    @PostMapping(value = "/{seshCode}/players/host")
    @ResponseBody
    public AbstractServiceState joinAsHost(@PathVariable String seshCode) {

        log.info("Started joinAsHost with seshCode={}", seshCode);
        try {
            AbstractServiceState hostState = seshService.joinAsHost(seshCode);
            log.info("Finished joinAsHost with seshCode={}, state={}", seshCode, hostState);
            return hostState;
        } catch (Exception e) {
            log.warn("Finished joinAsHost with seshCode={}, error={}", seshCode, e.toString());
            throw e;
        }
    }

    @GetMapping("/{seshCode}/players/{playerName}/state")
    @ResponseBody
    public AbstractServiceState getPlayerState(@PathVariable String seshCode, @PathVariable String playerName){

        log.info("Started getPlayerState with seshCode={}, playerName={}", seshCode, playerName);
        try {
            AbstractServiceState state = seshService.getStateForPlayer(seshCode, playerName);
            log.info("Finished getPlayerState with seshCode={}, playerName={}, result={}", seshCode, playerName, state);
            return state;
        } catch (Exception e) {
            log.warn("Exiting getPlayerState with seshCode={}, playerName={}, error={}", seshCode, playerName, e);
            throw e;
        }
    }
}
