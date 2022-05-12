package com.software.triviabot.cache;

import com.software.triviabot.enums.Hint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class HintCache { // map of users with all of their remaining hint options
    private static Map<Long, Map<Hint, Integer>> hintCacheMap = new HashMap<>();
    private static final int startHintNumber = 2;

    private HintCache(){}

    public static void setUpCache(long userId){
        // populating the hint map with unspent hints
        Map<Hint, Integer> hintMap = new HashMap<>();
        for (Hint hint : Hint.values()) {
            hintMap.put(hint, startHintNumber);
        }
        hintCacheMap.put(userId, hintMap);
    }

    public static int getRemainingHints(long userId, Hint hint){
        return hintCacheMap.get(userId).get(hint);
    }

    public static void decreaseHint(long userId, Hint hint){
        if (getRemainingHints(userId, hint) < 1)
            throw new IllegalArgumentException("Out of hints: " + hint.name());
        int prevHintNumber = hintCacheMap.get(userId).get(hint);
        hintCacheMap.get(userId).put(hint, prevHintNumber - 1);
    }

    public static void clearCache(long userId){
        log.info("Clearing hint cache for user {}", userId);
        hintCacheMap.remove(userId);
    }

}
