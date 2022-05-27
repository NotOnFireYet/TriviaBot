package com.software.triviabot.cache.loader;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.bot.handler.EventHandler;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.enums.State;
import com.software.triviabot.model.User;
import com.software.triviabot.model.UserCache;
import com.software.triviabot.repo.object.UserCacheRepo;
import com.software.triviabot.repo.object.UserRepo;
import com.software.triviabot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PreDestroy;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
// loads all user cache into db on every graceful shutdown
// and uploads it back into cache classes on every launch
public class CacheLoader {
    private final UserCacheRepo cacheRepo;
    private final UserRepo userRepo;
    private final EventHandler eventHandler;
    private final ReplySender sender;
    private final MessageService msgService;

    @PreDestroy
    public void destroy() throws TelegramApiException {
        List<User> users = userRepo.getAllUsers();
        UserCache cache;
        long userId;
        long chatId;
        State state;
        for (User user : users) {
            userId = user.getUserId();
            cache = cacheRepo.findByUserId(userId) == null ? new UserCache() : cacheRepo.findByUserId(userId);
            cache.setUser(user);
            state = StateCache.getState(userId);
            if (state != null) {
                cache.setState(state.name());
                // if bot fell asleep during game
                if (state.equals(State.FIRSTQUESTION) || state.equals(State.GAMEPROCESS) ||
                    state.equals(State.RIGHTANSWER)) {
                    setGameCache(userId, cache);
                }
                if (ActiveMessageCache.getChatIdMap().containsKey(userId)) {
                    chatId = ActiveMessageCache.getChatByUserId(userId);
                    if (!state.equals(State.SCORE)) // to avoid deleting correct answer reaction from latest game
                        deleteActiveMessages(chatId, userId);
                    sender.send(eventHandler.getBotAsleepMessage(chatId));
                }
            }
            cacheRepo.saveCache(cache);
        }
        log.info("Saved cache for all users");
    }

    // saves all the game process parameters
    private UserCache setGameCache(long userId, UserCache cache) {
        cache.setQuestion(QuestionCache.getCurrentQuestion(userId));
        cache.setFiftyFiftyRemains(HintCache.getRemainingHints(userId, Hint.FIFTY_FIFTY));
        cache.setAudienceHelpRemains(HintCache.getRemainingHints(userId, Hint.AUDIENCE_HELP));
        cache.setCallFriendRemains(HintCache.getRemainingHints(userId, Hint.CALL_FRIEND));
        return cache;
    }

    private void deleteActiveMessages(long chatId, long userId) {
        try { // either message may have already been deleted
            msgService.deleteMessage(chatId, ActiveMessageCache.getRefreshMessageId(userId));
        } catch (TelegramApiException | NullPointerException e) {
            log.error("Error: {}", e.getMessage());
        }
        try {
            msgService.deleteCachedMessage(chatId, userId);
        } catch (TelegramApiException | NullPointerException e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
