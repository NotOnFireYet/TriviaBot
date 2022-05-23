package com.software.triviabot.repo;

import com.software.triviabot.model.QuestionStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IQuestionStatsRepo extends JpaRepository<QuestionStat, Integer> {

}
