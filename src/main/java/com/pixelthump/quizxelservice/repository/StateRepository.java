package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, String> {

    State findBySeshCode(String seshCode);

    boolean existsBySeshCode(String seshCode);

    Optional<State> findBySeshCodeAndActive(String seshCode, Boolean active);

    List<State> findByActive(Boolean active);

}
