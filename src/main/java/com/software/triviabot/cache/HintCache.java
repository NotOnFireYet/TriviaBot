package com.software.triviabot.cache;

import com.software.triviabot.bot.enums.Hint;
import lombok.Getter;
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

    public static void setUpHints(long userId){
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

    public static void decreaseHint(long userId, Hint hintName){
        int prevHintNumber = hintCacheMap.get(userId).get(hintName); // todo: throw exception on negative hints
        hintCacheMap.get(userId).put(hintName, prevHintNumber - 1);
    }

    public static void deleteHintCache(long userId){
        hintCacheMap.remove(userId);
    }

}
