package com.pixelthump.quizxelservice.repository.model.command;
import com.pixelthump.quizxelservice.repository.model.State;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CommandId commandId)) return false;
        return Objects.equals(timestamp, commandId.timestamp) && Objects.equals(state, commandId.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, state);
    }
}
