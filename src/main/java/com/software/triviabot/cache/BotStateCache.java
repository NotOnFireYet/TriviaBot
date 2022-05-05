package com.software.triviabot.cache;

import com.software.triviabot.enums.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BotStateCache { // Keeps current bot state for each user id
    private static Map<Long, BotState> botStateMap = new HashMap<>();

    private BotStateCache(){}

    public static BotState getCurrentState(long userId){
        return botStateMap.get(userId);
    }

    public static void saveBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
        log.info("BotState for user {}: {}", userId, botState);
    }
}
