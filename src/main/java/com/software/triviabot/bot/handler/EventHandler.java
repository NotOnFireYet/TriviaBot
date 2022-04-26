package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.ScoreCache;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.User;
import com.software.triviabot.service.DAO.QuestionDAO;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final BotStateCache botStateCache;
    private final MenuService menuService;
    private final QuestionService questionService;
    private final QuestionCache questionCache;
    private final ScoreCache scoreCache;

    public SendMessage processAnswer(long chatId, long userId, boolean isCorrect) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (isCorrect) {
            message.setText("Правильно!");
            scoreCache.incrementScore(userId);
        }
        else
            message.setText("Вы пожалеете об этой ошибке.");

        // see if this was the last question
        if (!questionDAO.exists(questionCache.getNextQuestionId(userId))) {
            log.info("This was the last question");
            Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
            telegramBot.execute(message);
            return sendScoreMessage(chatId, userId);
        }
        message.setReplyMarkup(menuService.getNextQuestionKeyboard());
        return message;
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
        Question question = questionDAO.findQuestionById(questionId);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (question == null) { // if no more questions left
            return sendScoreMessage(chatId, userId);
        } else {
            message.setReplyMarkup(menuService.getQuestionKeyboard(question.getAnswers()));
            message.setText(question.getText());
        }
        return message;
    }

    public SendMessage sendScoreMessage(long chatId, long userId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы ответили на все вопросы! Ваш счет: " + scoreCache.getCurrentScoreMap().get(userId));
        message.setReplyMarkup(menuService.getTryAgainKeyboard());
        return message;
    }

    public BotApiMethod<?> sendStartMessage(long chatId, long userId) {
        String text = "Добрый день, дорогие друзья! \n" +
            "Сегодня на нашем канале шоу для тех, кто умеет логически мыслить и много знает:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Приветствуем наших игроков!"; // todo: implement name entering

        return menuService.getStartingMessage(chatId, userId, text);
    }

    public BotApiMethod<?> sendGamestartMessage(long chatId, long userId){
        SendMessage message = new SendMessage();
        message.setText("Начнем!");
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(menuService.getHintKeyboard());
        botStateCache.saveBotState(userId, BotState.SENDQUESTION);
        return message;
    }

}
