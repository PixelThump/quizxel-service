package com.pixelthump.quizxelservice.sesh;
import com.pixelthump.quizxelservice.sesh.model.state.question.BuzzerQuestion;
import com.pixelthump.quizxelservice.sesh.model.state.question.Question;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
@Log4j2
public class QuestionProvider {

    private final List<Question> questions;
    private int currentIndex;

    public QuestionProvider() {

        List<Question> localQuestions = new ArrayList<>();
        localQuestions.add(new BuzzerQuestion("1. Warum sind wir heute alle hier?", "Weil Thomas Geburtstag hat."));
        localQuestions.add(new BuzzerQuestion("2. Eines von Thomas Lieblingsspiele ist left4dead2. \n In diesem Spiel gibt es ein Achievement bei dem man einen Garten Zwerg retten muss. Wie heißt dieser Zwerg? ", "Gnome Chompski"));
        localQuestions.add(new BuzzerQuestion("3. Bleiben wir bei l4d2. Wie viele Special Infected gibt es in l4d2?", "//TODO: Add answer"));
        localQuestions.add(new BuzzerQuestion("4. Und wie heißen sie? 1 Infected pro Buzzer.", "//TODO: Add answer"));
        localQuestions.add(new BuzzerQuestion("5. Genug von l4d. Was thomas auch mag ist das MCU. Wofür steht das nochmal mcu?", "Marvel Cinematic Universe"));
        localQuestions.add(new BuzzerQuestion("6. Was war denn der erste releaste mcu Film?", "Iron Man"));
        localQuestions.add(new BuzzerQuestion("7. Und der neueste?", "Guardians of the Galaxy Vol. 3"));
        localQuestions.add(new BuzzerQuestion("8. In Iron Man 2 gab es ein kameo von einem Billionär. Wie heißt er?", "Elon Musk"));
        localQuestions.add(new BuzzerQuestion("9. Eine letzte mcu frage noch. Für welchen Satz ist Vin diesels Character bekannt?", "I am Groot/ Ich bin Groot"));
        localQuestions.add(new BuzzerQuestion("10. Gehen wir zurück zu Spiele. Thomas mag ja auch Nintendo. Was hat Nintendo bei ihrer gründung verkauft?", "Hanafuda-Spielkarten"));
        localQuestions.add(new BuzzerQuestion("11. Nintendo hat auch ein sehr bekanntes Maskotchen in rot. Wie heißt denn das ebenfalls bekannte blaue Maskotchen des Rivalen Sega?", "Sonic the Hedgehog"));
        localQuestions.add(new BuzzerQuestion("12. Wen hab ich mit rotes Maskottchen gemeint?", "Mario 'Jumpman' Mario"));
        localQuestions.add(new BuzzerQuestion("13. Und wie heißt sein grüner Bruder?", "Luigi Mario"));
        localQuestions.add(new BuzzerQuestion("14. Nintendo hat noch einen grünen Character in einer anderen Spiele Serie. Wie heißt seine Feenbegleiterin in Majoras Mask?", "Tael"));
        localQuestions.add(new BuzzerQuestion("15. Letzte frage: Was wünschen wir thomas heute?", "Alles gute zum Geburtstag!!"));
        this.questions = localQuestions;
    }

    public Question getCurrentQuestion() {

        return questions.get(currentIndex);
    }

    public void getNextQuestion() {

        currentIndex++;
        if (currentIndex >= (questions.size())) currentIndex--;
    }

    public void getPreviousQuestion() {

        currentIndex--;
        if (currentIndex < 0) currentIndex = 0;
    }

    public void getFirstQuestion() {

        currentIndex = 0;
    }
}
