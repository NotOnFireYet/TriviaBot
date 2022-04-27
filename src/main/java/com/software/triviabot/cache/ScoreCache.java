package com.software.triviabot.cache;

import com.software.triviabot.data.Score;
import com.software.triviabot.service.DAO.UserDAO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class ScoreCache {
    // keeps current score for each user id
    private final Map<Long, Score> currentScoreMap = new HashMap<>();

    @Autowired
    private final UserDAO userDAO;

    public void incrementScore(Long userId) {
        if (currentScoreMap.containsKey(userId)){ // if not first right answer, increment by 1
            Score prevScore = currentScoreMap.get(userId);
            currentScoreMap.get(userId).setPoints(prevScore.getPoints() + 1);
        } else { // if first right answer, set up score object for user
            Score score = new Score();
            score.setPoints(1);
            score.setUser(userDAO.findUserById(userId));
            currentScoreMap.put(userId, score);
        }
        log.info("Current score for user {}: {}", userId, currentScoreMap.get(userId).getPoints());
    }

    // deletes the user-score pair after a game is complete
    public void deleteScoreCache(Long userId) {
        currentScoreMap.remove(userId);
    }
}
