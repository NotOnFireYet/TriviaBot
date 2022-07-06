package com.software.triviabot.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.triviabot.model.*;
import com.software.triviabot.model.json.QuestionJson;
import com.software.triviabot.model.json.TopicJson;
import com.software.triviabot.repo.object.QuestionRepo;
import com.software.triviabot.repo.object.TopicRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionService {
    private final TopicRepo topicRepo;
    private String path = "json/topics.json";

    public void generateQuizData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TopicJson[] topics;
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            topics = mapper.readValue(file, TopicJson[].class);
        }  catch (NullPointerException e) {
            log.error("Could not locate quiz data file. Proceeding with previous version");
            return;
        } catch (JsonParseException e) {
            log.error("Failed to parse quiz data. Proceeding with previous version");
            return;
        }
        try {
            topicRepo.deleteAllTopics(); // delete and re-upload all quiz data on relaunch
            for (TopicJson t : topics) {
                Topic topic = new Topic();
                topic.setTitle(t.getTopicName());
                topic.setQuestions(new ArrayList<>());
                for (QuestionJson q : t.getQuestions()) {
                    topic.addQuestion(createQuestion(topic,
                        q.getQuestionText(),
                        q.getRightAnswerReaction(),
                        q.getAnswers(),
                        q.getRightAnswer()));
                }
                // give each question a number to keep order
                for (int i = 1; i <= topic.getQuestions().size(); i++) {
                    topic.getQuestions().get(i - 1).setNumberInTopic(i);
                }
                topicRepo.saveTopic(topic);
            }
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

    private Question createQuestion(Topic topic, String text, String correctAnswerReaction, List<String> answerTexts, String rightAnswerText) {
        Question question = new Question();
        question.setText(text);
        question.setTopic(topic);
        question.setCorrectAnswerReaction(correctAnswerReaction);
        question.setAnswers(createAnswers(question, answerTexts, rightAnswerText));
        return question;
    }

    private List<Answer> createAnswers(Question question, List<String> answerTexts, String rightAnswerText) throws IllegalArgumentException {
        if (!answerTexts.contains(rightAnswerText))
            throw new IllegalArgumentException("Answers do not contain the specified right answer text.");

        ArrayList<Answer> answerList = new ArrayList<>();
        boolean hasRightAnswer = false; // to prevent saving multiple right answers
        for (String answerText : answerTexts){
            Answer answer = new Answer();
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
