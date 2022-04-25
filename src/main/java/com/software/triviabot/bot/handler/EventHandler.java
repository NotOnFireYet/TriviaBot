package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.chache.BotStateCache;
import com.software.triviabot.data.User;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final BotStateCache botStateCache;
    private final MenuService menuService;

    @Autowired
    public EventHandler(UserDAO userDAO, BotStateCache botStateCache,
        MenuService menuService){
        this.userDAO = userDAO;
        this.botStateCache = botStateCache;
        this.menuService = menuService;
    }

    public SendMessage processAnswer(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы дали ответ. Интересно.");
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

    public SendMessage saveNewUser(long chatId, Message message, long userId, SendMessage sendMessage) {
        String userName = message.getFrom().getUserName();
        User user = new User();
        user.setId(userId);
        user.setUsername(userName);
        userDAO.saveUser(user);
        sendMessage.setText("Пользователь сохранен");
        sendMessage.setChatId(String.valueOf(chatId));
        botStateCache.saveBotState(userId, BotState.START);
        return sendMessage;
    }

    public BotApiMethod<?> sendQuestion(long chatId) {
        log.info("Sending question from EventHandler");
        String messageText = "Вопрос сос";
        SendMessage msg = new SendMessage();
        msg.setReplyMarkup(menuService.getInlineQuestionKeyboard());
        msg.setText(messageText);
        msg.setChatId(String.valueOf(chatId));
        return msg;
    }

    public BotApiMethod<?> sendStartMessage(long chatId, long userId) {
        log.info("Sending starting message from EventHandler");
        String messageText = "Добрый день, дорогие друзья! \n" +
            "Сегодня на нашем канале шоу для тех, кто умеет логически мыслить и много знает:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Приветствуем наших игроков!";
        return menuService.getStartingMessage(chatId, userId, messageText);
    }
}
