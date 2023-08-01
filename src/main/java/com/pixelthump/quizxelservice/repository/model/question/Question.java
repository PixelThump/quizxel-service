package com.pixelthump.quizxelservice.repository.model.question;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter

public abstract class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long packIndex;

    private String text;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionpack_id")
    private Questionpack questionpack;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Question question = (Question) o;
        return getId() != null && Objects.equals(getId(), question.getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
