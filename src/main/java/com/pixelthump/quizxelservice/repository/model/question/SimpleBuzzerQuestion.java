package com.pixelthump.quizxelservice.repository.model.question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@DiscriminatorValue("SimpleBuzzerQuestion")
@Getter
@Setter
public class SimpleBuzzerQuestion extends Question {

    String answer;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SimpleBuzzerQuestion that = (SimpleBuzzerQuestion) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
