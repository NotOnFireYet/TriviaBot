package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.cache.BotStateCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CallbackQueryHandler {
    private final EventHandler eventHandler;
    private final ReplySender sender;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws TelegramApiException {
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();

        String data = buttonQuery.getData();
        BotStateCache.saveBotState(userId, BotState.GETANSWER);
        log.info("Received callback data: {}", data);
        switch (data) {
            case ("answerCallbackCorrect"):
                eventHandler.processAnswer(chatId, userId, true);
                break;
            case ("answerCallbackWrong"):
                eventHandler.processAnswer(chatId, userId, false);
                break;
            case ("nextQuestionCallback"):
                BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
                eventHandler.sendNextQuestion(chatId, userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown callback");
        }
        return null;
    }
}
