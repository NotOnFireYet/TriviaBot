package com.software.triviabot.repo;

import com.software.triviabot.data.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuestionRepo extends JpaRepository<Question, Integer> {
}
