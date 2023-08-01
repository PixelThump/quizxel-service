package com.pixelthump.quizxelservice.repository.model.command;
import com.pixelthump.quizxelservice.repository.model.CommandId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "command_type", discriminatorType = DiscriminatorType.STRING)
public class Command {

    @EmbeddedId
    private CommandId commandId;
    private String playerid;
    private String type;
    private String body;
}
