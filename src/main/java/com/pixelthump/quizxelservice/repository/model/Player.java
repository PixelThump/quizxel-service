package com.pixelthump.quizxelservice.repository.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity

public class Player implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private String playerId;
    @Column(name = "player_name", nullable = false)
    private String playerName;
    @Column(name = "vip", nullable = false)
    private Boolean vip = false;
    @Column(name = "points")
    private Long points;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "sesh_code", nullable = false)
    private State state;

    public void addPoints(Integer pointsToAdd){

        this.points = points + pointsToAdd;
    }
}
