package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.config.HintConfig;
import com.software.triviabot.service.DAO.UserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageHandler {
    private final UserDAO userDAO;
    private final BotStateCache botStateCache;
    private final EventHandler eventHandler;
    private final QuestionCache questionCache;

    public BotApiMethod<?> handle(Message message, BotState botState) throws TelegramApiException {
        Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        // if new user
        if (!userDAO.exists(userId)) {
            eventHandler.saveNewUser(message.getFrom().getUserName(), userId);
            botStateCache.saveBotState(userId, BotState.ENTERNAME);
            return eventHandler.getStartMessage(chatId);
        }
        botStateCache.saveBotState(userId, botState); //save state to cache

        switch (botState) {
            case ENTERNAME:
                return eventHandler.processEnteredName(userId, chatId, message.getText());
            case GAMESTART:
                HintCache.setUpHints(userId);
                telegramBot.execute(eventHandler.getGamestartMessage(userId, chatId));
                return eventHandler.sendNextQuestion(chatId, userId);
            case SENDQUESTION:
                telegramBot.execute(eventHandler.getDontGetDistracted(chatId, userId));
                questionCache.decreaseQuestionId(userId);
                return eventHandler.sendNextQuestion(chatId, userId);
            case GIVEHINT:
                return eventHandler.processHintRequest(chatId, userId, HintConfig.getHintByText(message.getText()));
            default:
                throw new IllegalStateException("Unexpected value: " + botState);
        }
    }
}
