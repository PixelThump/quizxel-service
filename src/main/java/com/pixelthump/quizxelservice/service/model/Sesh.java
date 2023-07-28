package com.pixelthump.quizxelservice.service.model;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Sesh {

    @Getter
    private String seshCode;

    public void start(String seshCode){

        this.seshCode = seshCode;
    }
}
