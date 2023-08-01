package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.Questionpack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionPackRepository extends JpaRepository<Questionpack, String> {

    Questionpack findByPackName(String packName);

}
