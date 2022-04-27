package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.BotState;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.ScoreCache;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.Score;
import com.software.triviabot.data.User;
import com.software.triviabot.service.DAO.QuestionDAO;
import com.software.triviabot.service.DAO.ScoreDAO;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final ScoreDAO scoreDAO;
    private final MenuService menuService;

    private final QuestionCache questionCache;
    private final ScoreCache scoreCache;
    private final BotStateCache botStateCache;

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
            Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
            telegramBot.execute(message);
            return processScoreEvent(chatId, userId);
        }
        message.setReplyMarkup(menuService.getNextQuestionKeyboard());
        return message;
    }

    public SendMessage processScoreEvent(long chatId, long userId){
        Score score = scoreCache.getCurrentScoreMap().get(userId);
        scoreCache.deleteScoreCache(userId);
        questionCache.deleteQuestionCache(userId);
        scoreDAO.saveScore(score);
        userDAO.saveScoreToUser(userId, score);
        return getScoreMessage(chatId, score.getPoints());
    }

    public SendMessage saveNewUser(long chatId, Message message, long userId, SendMessage sendMessage) {
        String userName = message.getFrom().getUserName();
        User user = new User();
        user.setUserId(userId);
        user.setUsername(userName);
        userDAO.saveUser(user);
        sendMessage.setText("Пользователь сохранен");
        sendMessage.setChatId(String.valueOf(chatId));
        botStateCache.saveBotState(userId, BotState.START);
        return sendMessage;
    }

    // returns the next question according to QuestionCache
    public SendMessage sendNextQuestion(long chatId, long userId) {
        questionCache.incrementQuestionId(userId);
        int questionId = questionCache.getCurrentQuestionMap().get(userId);
        Question question = questionDAO.findQuestionById(questionId);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (question == null) { // if no more questions left
            return processScoreEvent(chatId, userId);
        } else {
            message.setReplyMarkup(menuService.getQuestionKeyboard(question.getAnswers()));
            message.setText(question.getText());
        }
        return message;
    }


    public SendMessage sendStartMessage(long chatId, long userId) {
        String text = "Добрый день, дорогие друзья! \n" +
            "Сегодня на нашем канале шоу для тех, кто умеет логически мыслить и много знает:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Приветствуем наших игроков!"; // todo: implement name entering

        return menuService.getStartingMessage(chatId, userId, text);
    }

    public SendMessage sendGamestartMessage(long chatId, long userId){
        SendMessage message = new SendMessage();
        message.setText("Начнем!");
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(menuService.getHintKeyboard());
        botStateCache.saveBotState(userId, BotState.SENDQUESTION);
        return message;
    }

    public SendMessage sendDontGetDistracted(long chatId, long userId) {
        SendMessage message = new SendMessage();
        message.setText("Давайте не будем отвлекаться.");
        message.setChatId(String.valueOf(chatId));
        return message;
    }

    private SendMessage getScoreMessage(long chatId, int points){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы ответили на все вопросы! Ваш счет: " + points);
        message.setReplyMarkup(menuService.getTryAgainKeyboard());
        return message;
    }
}
