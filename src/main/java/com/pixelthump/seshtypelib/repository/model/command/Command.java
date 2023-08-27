package com.pixelthump.seshtypelib.repository.model.command;
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
    private String playerName;
    private String type;
    private String body;
}
