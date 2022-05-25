package com.software.triviabot.cache;

import com.software.triviabot.model.UserCache;
import com.software.triviabot.repo.object.UserCacheRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ActiveMessageCache {
    // message to be edited during quiz.
    // whole quiz consists of refreshing this message
    private static Map<Long, Integer> refreshMessageMap = new HashMap<>();
    // message to be deleted
    private static Map<Long, Integer> deleteMessageMap = new HashMap<>();

    @Autowired
    private static UserCacheRepo cacheRepo; // todo: stopped on this throwing a nullpointer. there may be a way to have a custom shutdown hook.

    public static void setRefreshMessageId(long userId, int messageId){
        /*UserCache cache = cacheRepo.findByUserId(userId);
        cache.setRefreshMessageId(messageId);
        cacheRepo.saveCache(cache);*/

        refreshMessageMap.put(userId, messageId);
    }

    public static int getRefreshMessageId(long userId){
        return refreshMessageMap.get(userId);
    }

    public static int getDeleteMessageId(long userId){
        return deleteMessageMap.get(userId);
    }

    public static void setDeleteMessageId(long userId, int messageId) {
        /*UserCache cache = cacheRepo.findByUserId(userId);
        cache.setDeleteMessageId(messageId);
        cacheRepo.saveCache(cache);*/

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
