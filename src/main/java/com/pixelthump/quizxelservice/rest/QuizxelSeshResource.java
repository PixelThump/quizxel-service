package com.pixelthump.quizxelservice.rest;
import com.pixelthump.quizxelservice.rest.model.QuizxelSeshInfo;
import com.pixelthump.quizxelservice.service.SeshService;
import com.pixelthump.quizxelservice.service.model.SeshInfo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{seshCode}")
@Log4j2
public class QuizxelSeshResource {
    private final SeshService seshService;
    private final ModelMapper modelMapper;

    @Autowired
    public QuizxelSeshResource(SeshService seshService, ModelMapper modelMapper) {

        this.seshService = seshService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
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
    public QuizxelSeshInfo hostSesh(@PathVariable String seshCode){

        log.info("Started hostSesh with seshCode={}", seshCode);
        SeshInfo sesh = seshService.hostSesh(seshCode);
        QuizxelSeshInfo quizxelSesh = modelMapper.map(sesh, QuizxelSeshInfo.class);
        log.info("Finished hostSesh with seshCode={}, result={}", seshCode, quizxelSesh);

        return quizxelSesh;
    }
}
