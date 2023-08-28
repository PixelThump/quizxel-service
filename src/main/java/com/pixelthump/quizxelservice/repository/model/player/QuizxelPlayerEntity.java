package com.pixelthump.quizxelservice.repository.model.player;
import com.pixelthump.seshtypelib.service.model.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
public class QuizxelPlayerEntity extends Player implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "player_icon_name")
    private PlayerIconName playerIconName;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof QuizxelPlayerEntity that)) return false;
        return playerIconName == that.playerIconName;
    }

    @Override
    public int hashCode() {

        return Objects.hash(playerIconName);
    }
}
