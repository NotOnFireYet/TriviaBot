package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.Topic;
import com.software.triviabot.repo.IQuestionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public int getNumberOfQuestions(){
        log.info("Fetching overall number of questions");
        return findAllQuestions().size();
    }

    public void saveQuestion(Question question){
        log.info("Saving question with id {}", question.getQuestionId());
        questionRepo.save(question);
    }

    public Boolean exists(int id){
        log.info("Checking if question {} exists", id);
        return questionRepo.existsById(id);
    }

    public void deleteQuestion(Question question){
        log.info("Deleting question {}", question.getQuestionId());
        questionRepo.delete(question);
    }

    public Question createQuestion(Topic topic, String text, String correctAnswerReaction, List<String> answerTexts, String rightAnswerText) {
        Question question = new Question();
        question.setText(text);
        question.setTopic(topic);
        question.setCorrectAnswerReaction(correctAnswerReaction);
        ArrayList<Answer> answerList = new ArrayList<>();

        boolean hasRightAnswer = false; // to prevent saving multiple right answers
        for (String answerText : answerTexts){
            Answer answer = new Answer();
            answer.setPercentagePicked(new Random().nextInt(100));
            answer.setText(answerText);
            answer.setQuestion(question);
            answer.setIsCorrect(answerText.equals(rightAnswerText));

            if (answer.getIsCorrect()){
                if (hasRightAnswer)
                    throw new IllegalArgumentException("Cannot create question with >1 correct answers.");
                hasRightAnswer = true;
            }
            answerList.add(answer);
        }
        question.setAnswers(answerList);
        return question;
    }

    public Question saveQuestionInTopic(Question question, Topic topic) {
        log.info("Saving question {} to topic {}", question.getQuestionId(), topic.getTopicId());
        question.setNumberInTopic(topic.getQuestions().indexOf(question) + 1);
        log.info("Topic number set to {}", question.getNumberInTopic());
        question.setTopic(topic);
        questionRepo.save(question);
        return question;
    }
}
