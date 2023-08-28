package com.pixelthump.quizxelservice.repository.model;
import com.pixelthump.seshtypelib.service.model.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class QuizxelStateEntity extends State {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_question_pack_pack_name")
    private Questionpack selectedQuestionPack;
    @Enumerated(EnumType.STRING)
    @Column(name = "sesh_stage", nullable = false)
    private SeshStage seshStage;
    @Column(name = "current_question_index")
    private Long currentQuestionIndex;
    @Column(name = "buzzed_player_name")
    private String buzzedPlayerName;
    @Column(name = "show_question")
    private Boolean showQuestion;
    @Column(name = "show_answer")
    private Boolean showAnswer;
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "state_questionpacks", joinColumns = @JoinColumn(name = "state_sesh_code"), inverseJoinColumns = @JoinColumn(name = "questionpacks_pack_name"))
    private List<Questionpack> questionpacks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof QuizxelStateEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(selectedQuestionPack, that.selectedQuestionPack) && seshStage == that.seshStage && Objects.equals(currentQuestionIndex, that.currentQuestionIndex) && Objects.equals(buzzedPlayerName, that.buzzedPlayerName) && Objects.equals(showQuestion, that.showQuestion) && Objects.equals(showAnswer, that.showAnswer) && Objects.equals(questionpacks, that.questionpacks);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), selectedQuestionPack, seshStage, currentQuestionIndex, buzzedPlayerName, showQuestion, showAnswer, questionpacks);
    }
}
