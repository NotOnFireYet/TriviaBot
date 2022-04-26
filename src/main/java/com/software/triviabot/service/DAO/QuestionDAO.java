package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Question;
import com.software.triviabot.repo.IQuestionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class QuestionDAO { // data access object.
    // idk if i need it for anything other than logging. leave if complex queries needed
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
        log.info("Saving question {}", question.getQuestionId());
        questionRepo.save(question);
    }

    public void deleteQuestion(Question question){
        log.info("Deleting question {}", question.getQuestionId());
        questionRepo.delete(question);
    }
}
