package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {
    private final BotStateCache botStateCache;
    private final MenuService menuService;
    private final EventHandler eventHandler;

    @Autowired
    public CallbackQueryHandler(BotStateCache botStateCache, MenuService menuService, EventHandler eventHandler) {
        this.botStateCache = botStateCache;
        this.menuService = menuService;
        this.eventHandler = eventHandler;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer;

        String data = buttonQuery.getData();
        botStateCache.saveBotState(userId, BotState.GETANSWER);
        switch (data) {
            case ("answerCallback"):
                callBackAnswer = eventHandler.processAnswer(chatId, buttonQuery.getMessage());
                break;
            case ("nextQuestionCallback"):
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Следующий вопрос я не дам.");
                break;
            default:
                throw new IllegalArgumentException("Unknown callback");
        }
        return callBackAnswer;
    }
}
