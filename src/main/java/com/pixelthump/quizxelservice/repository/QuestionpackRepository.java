package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionpackRepository extends JpaRepository<Questionpack, String> {

    Optional<Questionpack> findByPackName(String packName);
}