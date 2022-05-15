package com.software.triviabot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActiveMessageCache {
    // message to be edited during quiz.
    // whole quiz consists of refreshing this message
    private static Map<Long, Message> refreshMessageMap = new HashMap<>();

    // message to be deleted
    private static Map<Long, Message> deleteMessageMap = new HashMap<>();

    private ActiveMessageCache(){}

    public static void setRefreshMessage(long userId, Message message){
        refreshMessageMap.put(userId, message);
    }

    public static int getRefreshMessageId(long userId){
        return refreshMessageMap.get(userId).getMessageId();
    }

    public static Message getDeleteMessage(long userId){
        return deleteMessageMap.get(userId);
    }

    public static void setDeleteMessage(long userId, Message message) {
        deleteMessageMap.put(userId, message);
    }

    public static void clearCache(long userId) {
        log.info("Clearing active message cache for user {}", userId);
        if (refreshMessageMap.containsKey(userId))
            refreshMessageMap.remove(userId);
        if (deleteMessageMap.containsKey(userId))
        deleteMessageMap.remove(userId);
    }
}
