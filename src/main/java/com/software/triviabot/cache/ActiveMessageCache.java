package com.software.triviabot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActiveMessageCache {
    // message to be edited during quiz.
    // whole quiz consists of refreshing this message
    private static Map<Long, Integer> refreshMessageMap = new HashMap<>();

    // message to be deleted
    private static Map<Long, Integer> deleteMessageMap = new HashMap<>();

    // map with all the chat id's corresponding to users
    private static Map<Long, Long> chatIdMap = new HashMap<>();

    public ActiveMessageCache(){}

    public static void setChadId(long userId, long chatId) {
        chatIdMap.put(userId, chatId);
    }

    public static Map<Long, Long> getChatIdMap() {
        return chatIdMap;
    }

    public static long getChatByUserId(long userId) {
        return chatIdMap.get(userId);
    }

    public static void setRefreshMessageId(long userId, int messageId){
        refreshMessageMap.put(userId, messageId);
    }

    public static int getRefreshMessageId(long userId){
        return refreshMessageMap.get(userId);
    }

    public static int getDeleteMessageId(long userId){
        return deleteMessageMap.get(userId);
    }

    public static void setDeleteMessageId(long userId, int messageId) {
        deleteMessageMap.put(userId, messageId);
    }

    public static void clearCache(long userId) {
        log.info("Clearing active message cache for user {}", userId);
        if (refreshMessageMap.containsKey(userId))
            refreshMessageMap.remove(userId);
        if (deleteMessageMap.containsKey(userId))
        deleteMessageMap.remove(userId);
    }
}
