package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Score;
import com.software.triviabot.repo.IScoreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScoreDAO {
    private final IScoreRepo scoreRepo;
    private final EntityManager entityManager;

    public Score findScoreById(int scoreId){
        log.info("Fetching score with ID {}", scoreId);
        return scoreRepo.getById(scoreId);
    }

    public List<Score> findAllUsers(){
        log.info("Fetching all scores");
        return scoreRepo.findAll();
    }

    public Boolean exists(int scoreId){
        log.info("Checking if score {} exists", scoreId);
        List<?> resultSet = entityManager.createQuery(
            "SELECT s FROM Score s WHERE score_id=" + scoreId)
            .getResultList();
        return resultSet.isEmpty() ? false : true;
    }

    public void saveScore(Score score){
        log.info("Saving score {}", score.getScoreId());
        scoreRepo.save(score);
    }

    public void deleteUser(Score score){
        log.info("Deleting score {}", score.getScoreId());
        scoreRepo.delete(score);
    }
}
