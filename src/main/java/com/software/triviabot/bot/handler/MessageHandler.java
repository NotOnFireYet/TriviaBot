package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.chache.BotStateCache;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.QuestionDAO;
import com.software.triviabot.service.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class MessageHandler {
    private final QuestionDAO questionDAO;
    private final MenuService menuService;
    private final UserDAO userDAO;
    private final BotStateCache botStateCache;
    private final EventHandler eventHandler;

    @Autowired
    public MessageHandler(QuestionDAO questionDAO, UserDAO userDAO, MenuService menuService,
        EventHandler eventHandler, BotStateCache botStateCache) {
        this.questionDAO = questionDAO;
        this.menuService = menuService;
        this.userDAO = userDAO;
        this.botStateCache = botStateCache;
        this.eventHandler = eventHandler;
    }

    public BotApiMethod<?> handle(Message message, BotState botState) {
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
            case ("SENDQUESTION"):
                log.info("Got to sending question");
                return eventHandler.sendQuestion(chatId);
            case ("GIVEANSWER"):
                return eventHandler.processAnswer(chatId);
            default:
                throw new IllegalStateException("Unexpected value: " + botState);
        }
    }

}
