package com.software.triviabot.service;

import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.service.DAO.QuestionDAO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
public class QuestionService { // Class for editing the question data in the database
    private final QuestionDAO questionDAO;

    public QuestionService(QuestionDAO questionDAO){
        this.questionDAO = questionDAO;
    }

    public Question createQuestion(String text, List<String> answerTexts, String rightAnswerText){
        Question question = new Question();
        //question.setQuestionId(1);
        question.setText(text);
        ArrayList<Answer> answerList = new ArrayList<>();

        for (String answerText : answerTexts){
            Answer answer = new Answer();
            answer.setText(answerText);
            answer.setQuestion(question);
            if (answerText.equals(rightAnswerText))
                answer.setIsCorrect(true);

            answerList.add(answer);
        }
        question.setAnswers(answerList);
        questionDAO.saveQuestion(question);

        return question;
    }
}
