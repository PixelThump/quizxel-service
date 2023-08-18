package com.pixelthump.quizxelservice.config;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import com.pixelthump.quizxelservice.repository.model.player.PlayerId;
import com.pixelthump.quizxelservice.rest.model.QuizxelPlayer;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper getModelMapper() {

        ModelMapper mapper = new ModelMapper();
        TypeMap<QuizxelPlayer, Player> propertyMapper = mapper.createTypeMap(QuizxelPlayer.class, Player.class);
        return mapper;
    }
}
