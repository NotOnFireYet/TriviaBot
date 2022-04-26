package com.software.triviabot.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Getter
public class ScoreCache {
    // keeps current score for each user id
    private final Map<Long, Integer> currentScoreMap = new HashMap<>();

    public void incrementScore(Long userId) {
        if (currentScoreMap.containsKey(userId)){
            int prevScore = currentScoreMap.get(userId);
            currentScoreMap.put(userId, prevScore+1);
        } else {
            currentScoreMap.put(userId, 1);
        }
        log.info("Current score for user {}: {}", userId, currentScoreMap.get(userId));
    }

    // deletes the user score after a game is complete
    public void deleteScoreCache(Long userId) {
        currentScoreMap.remove(userId);
    }
}
