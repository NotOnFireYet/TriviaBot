package com.software.triviabot.repo.object;

import com.software.triviabot.model.Score;
import com.software.triviabot.repo.IScoreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScoreRepo {
    private final IScoreRepo scoreRepo;
    private final UserRepo userRepo;
    private final EntityManager entityManager;

    public List<Score> findScoresByUserId(long userId){
        log.info("Fetching all scores for user {}",userId);
        return entityManager.createQuery(
            "SELECT s FROM Score s WHERE user_id=" + userId)
            .getResultList();
    }

    @Transactional
    public void deleteUserScores(long userId) {
        log.info("Deleting all scores of user {}", userId);
        if (userRepo.findUserById(userId) != null) {
            entityManager.createQuery(
                "DELETE FROM Score s WHERE user_id=" + userId).executeUpdate();
        }
    }

    public Score saveScore(Score score){
        log.info("Saving score {}", score.getAnsweredQuestions());
        return scoreRepo.save(score);
    }
}
