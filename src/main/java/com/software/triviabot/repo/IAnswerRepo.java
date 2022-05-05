package com.software.triviabot.repo;

import com.software.triviabot.data.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAnswerRepo extends JpaRepository<Answer, Integer> { // todo: delet this?
}
