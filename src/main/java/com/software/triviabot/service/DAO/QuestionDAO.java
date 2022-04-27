package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.repo.IQuestionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionDAO {
    private final IQuestionRepo questionRepo;
    private final EntityManager entityManager;

    public Question findQuestionById(int id){
        log.info("Fetching question with ID {}", id);
        return questionRepo.getById(id);
    }

    public List<Question> findAllQuestions(){
        log.info("Fetching all questions");
        return questionRepo.findAll();
    }

    public void saveQuestion(Question question){
        log.info("Saving question");
        questionRepo.save(question);
    }

    public Boolean exists(int id){
        log.info("Checking if question {} exists", id);
        List<?> resultSet = entityManager.createQuery(
            "SELECT q FROM Question q WHERE question_id=" + id)
            .getResultList();
        return resultSet.isEmpty() ? false : true;
    }

    public void deleteQuestion(Question question){
        log.info("Deleting question {}", question.getQuestionId());
        questionRepo.delete(question);
    }

    public Question buildAndSaveQuestion(String text, List<String> answerTexts, String rightAnswerText){
        Question question = new Question();
        question.setText(text);
        ArrayList<Answer> answerList = new ArrayList<>();

        for (String answerText : answerTexts){
            Answer answer = new Answer();
            answer.setText(answerText);
            answer.setQuestion(question);
            answer.setIsCorrect(answerText.equals(rightAnswerText));
            answerList.add(answer);
        }
        question.setAnswers(answerList);
        saveQuestion(question);

        return question;
    }
}
