package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.QuestionCache;
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
            return eventHandler.saveNewUser(chatId, message, userId, sendMessage);
        }
        botStateCache.saveBotState(userId, botState); //save state to cache

        switch (botState.name()) {
            case ("START"):
                return eventHandler.sendStartMessage(chatId, userId);
            case ("GAMESTART"):
                telegramBot.execute(eventHandler.sendGamestartMessage(userId, chatId));
                return eventHandler.sendNextQuestion(chatId, userId);
            default:
                throw new IllegalStateException("Unexpected value: " + botState);
        }
    }

}
