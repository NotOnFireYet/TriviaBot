package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Answer;
import com.software.triviabot.repo.IAnswerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnswerDAO { // todo: delet this?
    private final IAnswerRepo answerRepo;

    public Answer findAnswerById(int id){
        log.info("Fetching answer with ID {}", id);
        return answerRepo.getById(id);
    }

    public List<Answer> findAllAnswers(){
        log.info("Fetching all answers");
        return answerRepo.findAll();
    }

    public int getNumberOfAnswers(){
        log.info("Fetching overall number of answers");
        return findAllAnswers().size();
    }

    public void saveAnswer(Answer answer){
        log.info("Saving answer");
        answerRepo.save(answer);
    }

    public Boolean exists(int id){
        log.info("Checking if answer {} exists", id);
        return answerRepo.existsById(id);
    }

    public void deleteAnswer(Answer answer){
        log.info("Deleting answer {}", answer.getAnswerId());
        answerRepo.delete(answer);
    }
}
