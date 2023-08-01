package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
