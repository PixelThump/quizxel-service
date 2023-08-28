package com.pixelthump.quizxelservice.repository.model;
import com.pixelthump.quizxelservice.repository.model.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity

public class Questionpack implements Serializable {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "questionpack", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("packIndex")
    private List<Question<?>> questions = new ArrayList<>();
    @Id
    @Column(name = "pack_name", nullable = false)
    private String packName;
    @ManyToMany(mappedBy = "questionpacks", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<QuizxelStateEntity> states = new ArrayList<>();


}
