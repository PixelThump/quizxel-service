package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.sesh.Sesh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SeshFactoryImpl implements SeshFactory{

    private final ApplicationContext applicationContext;

    @Autowired
    public SeshFactoryImpl(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    public Sesh createSesh(String seshCode){

        Sesh sesh = applicationContext.getBean(Sesh.class);
        sesh.start(seshCode);
        return sesh;
    }
}
