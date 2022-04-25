package com.software.triviabot.bot;

import com.software.triviabot.bot.handler.CallbackQueryHandler;
import com.software.triviabot.bot.handler.MessageHandler;
import com.software.triviabot.chache.BotStateCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramFacade {
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final BotStateCache botStateCache;

    @Autowired
    public TelegramFacade(MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, BotStateCache botStateCache){
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.botStateCache = botStateCache;

    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                return handleInputMessage(message);
            }
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        BotState botState;
        String inputText = message.getText();

        switch (inputText) {
            case "/start":
                botState = BotState.START;
                break;
            case "Начать викторину":
                botState = BotState.SENDQUESTION;
                break;
            default:
                botState = botStateCache.getBotStateMap().get(message.getFrom().getId()) == null?
                    BotState.START: botStateCache.getBotStateMap().get(message.getFrom().getId());
        }

        return messageHandler.handle(message, botState);

    }
}
