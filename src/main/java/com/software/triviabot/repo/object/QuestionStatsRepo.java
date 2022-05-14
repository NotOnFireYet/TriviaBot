package com.software.triviabot.repo.object;

import com.software.triviabot.data.QuestionStat;
import com.software.triviabot.repo.IQuestionStatsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionStatsRepo {
    private final IQuestionStatsRepo statsRepo;
    private final EntityManager entityManager;

    public QuestionStat saveStat(QuestionStat stat) {
        log.info("Saving stat for question {}", stat.getQuestion().getQuestionId());
        return statsRepo.save(stat);
    }

    public QuestionStat getByUserAndQuestionId(long userId, int questionId){
        log.info("Fetching stat for user {} and question {}", userId, questionId);
        List<QuestionStat> list = entityManager.createQuery(
            "SELECT q FROM QuestionStat q WHERE user_id=" + userId + " AND question_id=" + questionId)
            .getResultList();
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<QuestionStat> getAllStatsForQuestion(long questionId, long userId) {
        log.info("Getting all stats for question {} except for those with user {}", questionId, userId);
        List<QuestionStat> list = entityManager.createQuery(
            "SELECT q FROM QuestionStat q WHERE question_id=" + questionId
                + "AND NOT user_id=" + userId)
            .getResultList();
        return !list.isEmpty() ? list : new ArrayList<>();
    }

    public boolean hasStat(long userId, long questionId) {
        List<QuestionStat> list = entityManager.createQuery(
            "SELECT q FROM QuestionStat q WHERE question_id=" + questionId +
                "AND user_id=" + userId).getResultList();
        return !list.isEmpty();
    }

    @Transactional
    public void deleteAllUserStats(long userId) {
        entityManager.createQuery("DELETE FROM QuestionStat q WHERE user_id=" + userId)
            .executeUpdate();
    }
}