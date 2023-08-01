package com.pixelthump.quizxelservice.repository.model;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class CommandId implements Serializable {

    private LocalDateTime timestamp;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "state_sesh_code", nullable = false)
    private State state;

    public CommandId(State state) {

        this.state = state;
        this.timestamp = LocalDateTime.now();
    }
}
