package com.software.triviabot.cache;

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
    private static Map<Long, Integer> currentTopicMap = new HashMap<>();

    private QuestionCache(){}

    public static void setTopic(long userId, int topicId){
        currentQuestionMap.put(userId, 0);
        currentTopicMap.put(userId, topicId);
        log.info("Current topic for user {}: {}", userId, topicId);
    }

    public static int getCurrentTopicId(long userId){
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


    public static int getNextQuestionNum(Long userId){
        return currentQuestionMap.get(userId) + 1;
    }

    // deletes the record for user after a game is complete
    public static void deleteQuestionCache(Long userId) {
        if (currentQuestionMap.containsKey(userId)) {
            currentQuestionMap.remove(userId);
        }
        if (currentTopicMap.containsKey(userId)) {
            currentTopicMap.remove(userId);
        }
    }
}