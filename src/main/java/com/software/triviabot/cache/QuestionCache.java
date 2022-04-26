package com.software.triviabot.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Getter
public class QuestionCache {
    // Keeps current question id for each user id
    // keeps track of what question to send next
    // todo: make reset for user after each game
    private final Map<Long, Integer> currentQuestionMap = new HashMap<>();

    public void incrementQuestionId(Long userId) {
        if (currentQuestionMap.containsKey(userId)){
            int prevQuestion = currentQuestionMap.get(userId);
            currentQuestionMap.put(userId, prevQuestion+1);
        } else {
            currentQuestionMap.put(userId, 1);
        }
        log.info("Current question for user {}: {}", userId, currentQuestionMap.get(userId));
    }
}