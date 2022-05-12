package com.software.triviabot.service;

import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionService {

    public Question createQuestion(Topic topic, String text, String correctAnswerReaction, List<String> answerTexts, String rightAnswerText) {
        Question question = new Question();
        question.setText(text);
        question.setTopic(topic);
        question.setCorrectAnswerReaction(correctAnswerReaction);
        question.setAnswers(createAnswers(question, answerTexts, rightAnswerText));
        return question;
    }

    private List<Answer> createAnswers(Question question, List<String> answerTexts, String rightAnswerText) {
        ArrayList<Answer> answerList = new ArrayList<>();
        boolean hasRightAnswer = false; // to prevent saving multiple right answers
        for (String answerText : answerTexts){
            Answer answer = new Answer();
            answer.setPercentPicked(new Random().nextInt(100));
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
        return answerList;
    }
}
