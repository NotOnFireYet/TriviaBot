package com.software.triviabot.repo.object;

import com.software.triviabot.data.Answer;
import com.software.triviabot.repo.IAnswerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnswerRepo {
    private final IAnswerRepo answerRepo;
    public Answer findById(int answerId) {
        return answerRepo.getById(answerId);
    }
}
