package com.pixelthump.quizxelservice.repository.model;
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
    @OneToMany(mappedBy = "seshCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    @Column(name = "current_question_index")
    private Long currentQuestionIndex;
    @Column(name = "buzzed_player_id")
    private String buzzedPlayerId;
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

    public void nextQuestion() {

        currentQuestionIndex++;
        if (currentQuestionIndex >= (selectedQuestionPack.getQuestions().size())) currentQuestionIndex--;
    }

    public void prevQuestion() {

        currentQuestionIndex++;
        if (currentQuestionIndex++ < 0) currentQuestionIndex = 0L;
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
