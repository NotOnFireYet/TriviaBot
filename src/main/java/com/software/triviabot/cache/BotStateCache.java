package com.software.triviabot.cache;

import com.software.triviabot.bot.BotState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Getter
public class BotStateCache {
    // Keeps current bot state for each user id
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(Long userId, BotState botState) {
        botStateMap.put(userId, botState);
        log.info("BotState for user {}: {}", userId, botState);
    }
}
