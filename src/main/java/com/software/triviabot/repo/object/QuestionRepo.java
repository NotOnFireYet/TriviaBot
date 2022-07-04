package com.software.triviabot.repo.object;

import com.software.triviabot.model.Question;
import com.software.triviabot.repo.IQuestionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionRepo {
    public final IQuestionRepo questionRepo;
    private final EntityManager entityManager;

    public boolean existsByText(String text){
        log.info("Checking if question exists");
        List<Question> result = entityManager.createQuery(
                "SELECT q FROM Question q WHERE text=" + text)
            .getResultList();
        return !result.isEmpty();
    }

    public Question getByText(String text){
        log.info("Getting question by text");
        List<Question> result = entityManager.createQuery(
                "SELECT q FROM Question q WHERE text=" + text)
            .getResultList();
        return result.get(0);
    }
}
