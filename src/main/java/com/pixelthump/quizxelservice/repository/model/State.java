package com.pixelthump.quizxelservice.repository.model;
import com.pixelthump.quizxelservice.repository.model.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
public class State implements Serializable {

    @Id
    @Column(name = "sesh_code", nullable = false)
    private String seshCode;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_question_pack_pack_name")
    private Questionpack selectedQuestionPack;
    @Enumerated(EnumType.STRING)
    @Column(name = "sesh_stage", nullable = false)
    private SeshStage seshStage;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    @Column(name = "current_question_index")
    private Long currentQuestionIndex;
    @Column(name = "buzzed_player_name")
    private String buzzedPlayerName;
    @Column(name = "max_player", nullable = false)
    private Long maxPlayer;
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    @Column(name = "host_id")
    private String hostId;
    @Column(name = "show_question")
    private Boolean showQuestion;
    @Column(name = "show_answer")
    private Boolean showAnswer;
    @Column(name = "has_changed", nullable = false)
    private Boolean hasChanged = false;

    public void nextQuestion() {

        long questionPackSize = selectedQuestionPack.getQuestions().size();
        currentQuestionIndex += 1;
        if (currentQuestionIndex >= questionPackSize) currentQuestionIndex = questionPackSize - 1;
    }

    public void prevQuestion() {

        currentQuestionIndex -= 1;
        if (currentQuestionIndex <= 0) currentQuestionIndex = 0L;
    }

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
