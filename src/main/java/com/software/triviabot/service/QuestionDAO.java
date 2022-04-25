package com.software.triviabot.service;

import com.software.triviabot.data.Question;
import com.software.triviabot.repo.IQuestionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class QuestionDAO {
    private final IQuestionRepo questionRepo;

    @Autowired
    public QuestionDAO(IQuestionRepo questionRepo){
        this.questionRepo = questionRepo;
    }

    public Question findQuestionById(Long id){
        log.info("Fetching question with ID {}", id);
        return questionRepo.getById(id);
    }

    public List<Question> findAllQuestions(){
        log.info("Fetching all questions");
        return questionRepo.findAll();
    }

    public void saveQuestion(Question question){
        log.info("Saving question {}", question.getId());
        questionRepo.save(question);
    }

    public void deleteQuestion(Question question){
        log.info("Deleting question {}", question.getId());
        questionRepo.delete(question);
    }
}
