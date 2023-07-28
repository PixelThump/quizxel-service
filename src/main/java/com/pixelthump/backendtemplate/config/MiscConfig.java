package com.pixelthump.backendtemplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class MiscConfig {

    @Bean
    Random getSystemWideRandom() {

        return new Random();
    }
}
