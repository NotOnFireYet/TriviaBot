package com.software.triviabot.cache;

import com.software.triviabot.enums.State;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StateCache { // Keeps current bot state for each user id
    private static Map<Long, State> stateMap = new HashMap<>();

    private StateCache(){}

    public static Map<Long, State> getStateMap() {
        return stateMap;
    }

    public static State getState(long userId){
        return stateMap.get(userId);
    }

    public static void setState(long userId, State state) {
        stateMap.put(userId, state);
        log.info("State for user {}: {}", userId, state);
    }

    public static void clearCache(long userId){
        log.info("Clearing state cache for user {}", userId);
        if (stateMap.containsKey(userId))
            stateMap.remove(userId);
    }
}
