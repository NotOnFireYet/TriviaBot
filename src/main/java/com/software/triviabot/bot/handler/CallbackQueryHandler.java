package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.chache.BotStateCache;
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

        BotApiMethod<?> callBackAnswer = null;

        String data = buttonQuery.getData();

        switch (data) {
            case ("answer1"):
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Вы тыкнули на 1-й вариант ответа.");
                botStateCache.saveBotState(userId, BotState.GETANSWER);
                break;
            case ("answer2"):
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Вы тыкнули на 2-й вариант ответа.");
                botStateCache.saveBotState(userId, BotState.GETANSWER);
                break;
            case ("answer3"):
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Вы тыкнули на 3-й вариант ответа.");
                botStateCache.saveBotState(userId, BotState.GETANSWER);
                break;
            case ("answer4"):
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Вы тыкнули на 4-й вариант ответа.");
                botStateCache.saveBotState(userId, BotState.GETANSWER);
                break;
        }
        return callBackAnswer;
    }
}
