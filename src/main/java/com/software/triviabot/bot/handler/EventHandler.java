package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.User;
import com.software.triviabot.service.DAO.QuestionDAO;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;

@Slf4j
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final BotStateCache botStateCache;
    private final MenuService menuService;
    private final QuestionService questionService;

    @Autowired
    public EventHandler(UserDAO userDAO, QuestionDAO questionDAO,
        BotStateCache botStateCache, MenuService menuService, QuestionService questionService){
        this.userDAO = userDAO;
        this.questionDAO = questionDAO;
        this.botStateCache = botStateCache;
        this.menuService = menuService;
        this.questionService = questionService;
    }

    public SendMessage processAnswer(long chatId, Message answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы дали ответ: " + answer.getText());
        sendMessage.setReplyMarkup(menuService.getNextQuestionKeyboard());
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

    public BotApiMethod<?> sendQuestion(long chatId, long questionId) {
        questionService.createQuestion("2+2=?", Arrays.asList("1", "2", "3", "4"), "4");
        Question question = questionDAO.findQuestionById(questionId);
        String messageText = question.getText();
        SendMessage message = new SendMessage();
        message.setReplyMarkup(menuService.getQuestionKeyboard(question.getAnswers()));
        message.setText(messageText);
        message.setChatId(String.valueOf(chatId));
        return message;
    }

    public BotApiMethod<?> sendStartMessage(long chatId, long userId) {
        String messageText = "Добрый день, дорогие друзья! \n" +
            "Сегодня на нашем канале шоу для тех, кто умеет логически мыслить и много знает:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Приветствуем наших игроков!";

        return menuService.getStartingMessage(chatId, userId, messageText);
    }


}
