package com.software.triviabot.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class QuestionCache {
    // keeps current question id for each user
    // keeps track of what question to send next
    private static Map<Long, Integer> currentQuestionMap = new HashMap<>();

    public static void incrementQuestionId(Long userId) {
        if (currentQuestionMap.containsKey(userId)){
            int prevQuestion = currentQuestionMap.get(userId);
            currentQuestionMap.put(userId, prevQuestion + 1);
        } else {
            currentQuestionMap.put(userId, 1);
        }
        log.info("Current question for user {}: {}", userId, currentQuestionMap.get(userId));
    }

    public static void decreaseQuestionId(long userId) {
        int prevQuestion = currentQuestionMap.get(userId);
        currentQuestionMap.put(userId, prevQuestion - 1);
    }

    public static int getCurrentQuestionId(long userId) {
        return currentQuestionMap.get(userId);
    }


    public static int getNextQuestionId(Long userId){
        return currentQuestionMap.get(userId) + 1;
    }

    // deletes the record for user after a game is complete
    public static void deleteQuestionCache(Long userId) {
        currentQuestionMap.remove(userId);
    }
}