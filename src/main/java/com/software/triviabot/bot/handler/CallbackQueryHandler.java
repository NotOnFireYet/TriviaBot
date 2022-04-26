package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
public class CallbackQueryHandler {
    private final BotStateCache botStateCache;
    private final EventHandler eventHandler;

    @Autowired
    public CallbackQueryHandler(BotStateCache botStateCache, EventHandler eventHandler) {
        this.botStateCache = botStateCache;
        this.eventHandler = eventHandler;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer;

        String data = buttonQuery.getData();
        botStateCache.saveBotState(userId, BotState.GETANSWER);
        log.info("Received callback data: {}", data);
        switch (data) {
            case ("answerCallbackCorrect"):
                callBackAnswer = eventHandler.processAnswer(chatId, true);
                break;
            case ("answerCallbackWrong"):
                callBackAnswer = eventHandler.processAnswer(chatId, false);
                break;
            case ("nextQuestionCallback"):
                botStateCache.saveBotState(userId, BotState.SENDQUESTION);
                callBackAnswer = eventHandler.sendNextQuestion(chatId, userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown callback");
        }
        return callBackAnswer;
    }
}
