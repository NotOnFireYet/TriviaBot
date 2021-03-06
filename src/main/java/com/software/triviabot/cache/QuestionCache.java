package com.software.triviabot.cache;

import com.software.triviabot.model.Question;
import com.software.triviabot.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// keeps current question number and current topic for each user
// keeps track of what question to send next
// and how much money it's worth
@Slf4j
@Service
public class QuestionCache {
    private static Map<Long, Integer> currentQuestionMap = new HashMap<>();
    private static Map<Long, Topic> currentTopicMap = new HashMap<>();

    private QuestionCache(){}

    public static void setUpCache(long userId, Topic topic){
        currentQuestionMap.put(userId, 0);
        currentTopicMap.put(userId, topic);
        log.info("Current topic for user {}: {}", userId, topic.getTopicId());
    }

    public static Question getCurrentQuestion(long userId) throws NullPointerException {
        int num = currentQuestionMap.get(userId);
        Topic topic = currentTopicMap.get(userId);
        Question question = topic.getQuestionByNumber(num);
        if (question == null) {
            log.error("There is no question #{} in topic {}", num, topic.getTopicId());
            throw new NullPointerException("No such question exists");
        }
        return question;
    }

    public static Topic getCurrentTopic(long userId){
        return currentTopicMap.get(userId);
    }

    public static void incrementQuestionNum(long userId) {
        int prevQuestion = currentQuestionMap.get(userId);
        currentQuestionMap.put(userId, prevQuestion + 1);
        log.info("Current question for user {}: {}", userId, currentQuestionMap.get(userId));
    }

    public static void decreaseQuestionNum(long userId) {
        int prevQuestion = currentQuestionMap.get(userId);
        currentQuestionMap.put(userId, prevQuestion - 1);
    }

    public static int getCurrentQuestionNum(long userId) {
        return currentQuestionMap.get(userId);
    }

    public static boolean isLastQuestion(Long userId){
        Topic topic = currentTopicMap.get(userId);
        return topic.getQuestions().size() == getCurrentQuestionNum(userId);
    }

    public static void clearCache(Long userId) {
        log.info("Clearing question cache for user {}", userId);
        if (currentQuestionMap.containsKey(userId))
            currentQuestionMap.remove(userId);
        if (currentTopicMap.containsKey(userId))
            currentTopicMap.remove(userId);
    }

    public static void extractFromCache(long userId, Question question) {
        currentTopicMap.put(userId, question.getTopic());
        // -1 bc the question number will be incremented in updateQuestion()
        currentQuestionMap.put(userId, question.getNumberInTopic());
    }
}