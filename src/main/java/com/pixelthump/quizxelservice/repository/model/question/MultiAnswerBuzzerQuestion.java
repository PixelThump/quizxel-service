package com.pixelthump.quizxelservice.repository.model.question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DiscriminatorValue("MultiAnswerBuzzerQuestion")
@Getter
@Setter
public class MultiAnswerBuzzerQuestion extends Question<List<String>>{

    @ElementCollection
    @Column(name = "answer")
    @CollectionTable(name = "multi_answer_buzzer_question_answers", joinColumns = @JoinColumn(name = "multi_answer_buzzer_question_id"))
    private List<String> answer = new ArrayList<>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MultiAnswerBuzzerQuestion that = (MultiAnswerBuzzerQuestion) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
