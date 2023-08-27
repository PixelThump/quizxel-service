package com.pixelthump.seshtypelib.service.model;
import com.pixelthump.seshtypelib.service.model.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
public class State implements Serializable {

    @Id
    @Column(name = "sesh_code", nullable = false)
    private String seshCode;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    @Column(name = "max_player", nullable = false)
    private Long maxPlayer;
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    @Column(name = "host_joined")
    private boolean hostJoined;
    @Column(name = "has_changed", nullable = false)
    private Boolean hasChanged = false;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        State state = (State) o;
        return getSeshCode() != null && Objects.equals(getSeshCode(), state.getSeshCode());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
