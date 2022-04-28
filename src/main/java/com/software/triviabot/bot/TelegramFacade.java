package com.software.triviabot.bot;

import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.bot.handler.CallbackQueryHandler;
import com.software.triviabot.bot.handler.MessageHandler;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.container.HintContainer;
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
                QuestionCache.deleteQuestionCache(userId);
                HintCache.setUpHints(userId);
                botState = BotState.GAMESTART;
                break;
            default: // if first ever command, set to START, if not, leave botstate the same
                botState = BotStateCache.getCurrentState(userId) == null ?
                    BotState.START : BotStateCache.getCurrentState(userId);
        }

        // reaction to hint buttons
        if (inputText.equals(HintContainer.getHintText(Hint.AUDIENCE_HELP)) ||
            inputText.equals(HintContainer.getHintText(Hint.CALL_FRIEND)) ||
            inputText.equals(HintContainer.getHintText(Hint.FIFTY_FIFTY))) {
            botState = BotState.GIVEHINT;
        }

        return messageHandler.handle(message, botState);

    }
}
