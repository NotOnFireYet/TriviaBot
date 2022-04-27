package com.software.triviabot.bot;

import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.bot.handler.CallbackQueryHandler;
import com.software.triviabot.bot.handler.MessageHandler;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.config.HintConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TelegramFacade {
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final BotStateCache botStateCache;

    public BotApiMethod<?> handleUpdate(Update update) throws TelegramApiException {
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

    private BotApiMethod<?> handleInputMessage(Message message) throws TelegramApiException {
        BotState botState;
        long userId = message.getFrom().getId();
        String inputText = message.getText();

        switch (inputText) {
            case "/start":
                botState = BotState.START;
                break;
            case "Начать викторину":
                botState = BotState.GAMESTART;
                break;
            default: // if first ever command, set to START, if not, leave botstate the same
                botState = botStateCache.getBotStateMap().get(userId) == null ?
                    BotState.START : botStateCache.getBotStateMap().get(userId);
        }

        // reaction to hint buttons
        if (inputText.equals(HintConfig.getHintText(Hint.AUDIENCE_HELP)) ||
            inputText.equals(HintConfig.getHintText(Hint.CALL_FRIEND)) ||
            inputText.equals(HintConfig.getHintText(Hint.FIFTY_FIFTY))) {
            botState = BotState.GIVEHINT;
        }

        return messageHandler.handle(message, botState);

    }
}
