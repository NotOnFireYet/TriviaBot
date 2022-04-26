package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.QuestionCache;
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

@Slf4j
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final BotStateCache botStateCache;
    private final MenuService menuService;
    private final QuestionService questionService;
    private final QuestionCache questionCache;

    @Autowired
    public EventHandler(UserDAO userDAO, QuestionDAO questionDAO, QuestionCache questionCache,
        BotStateCache botStateCache, MenuService menuService, QuestionService questionService){
        this.userDAO = userDAO;
        this.questionDAO = questionDAO;

        this.questionCache = questionCache;
        this.botStateCache = botStateCache;

        this.menuService = menuService;
        this.questionService = questionService;
    }

    public SendMessage processAnswer(long chatId, boolean isCorrect) {
        SendMessage sendMessage = new SendMessage();
        if (isCorrect)
            sendMessage.setText("Правильно!");
        else
            sendMessage.setText("Вы пожалеете об этой ошибке.");
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

    // returns the next question according to QuestionCache
    public BotApiMethod<?> sendNextQuestion(long chatId, long userId) {
        questionCache.incrementQuestionId(userId);
        int questionId = questionCache.getCurrentQuestionMap().get(userId);
        Question question = questionDAO.findQuestionById(questionId); // todo: handle when it's a final question

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
