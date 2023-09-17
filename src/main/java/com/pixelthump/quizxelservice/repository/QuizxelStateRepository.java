package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.QuizxelStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizxelStateRepository extends JpaRepository<QuizxelStateEntity, String> {

    QuizxelStateEntity findBySeshCode(String seshCode);

    boolean existsBySeshCode(String seshCode);

    Optional<QuizxelStateEntity> findBySeshCodeAndActive(String seshCode, Boolean active);

    List<QuizxelStateEntity> findByActive(Boolean active);
}