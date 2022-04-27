package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.ScoreCache;
import com.software.triviabot.config.HintConfig;
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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    ////////////* NEW USER START EVENTS */////////////
    public SendMessage processEnteredName(long userId, long chatId, String name) {
        userDAO.saveNameToUser(userId, name);
        return getGreetingWithName(chatId, name);
    }

    private SendMessage getGreetingWithName(long chatId, String name){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Здравствуй, " + name + "!");
        message.setReplyMarkup(menuService.getStartQuizKeyboard());
        return message;
    }

    public void saveNewUser(String username, long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        userDAO.saveUser(user);
    }

    public SendMessage getStartMessage(long chatId) {
        String text = "Добрый день, дорогой друг! \n" +
            "Здесь тебе предстоит попробовать свои силы в игре:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Пожалуйста, введи свое имя:"; // todo: acceptable input is text
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(String.valueOf(chatId));
        return message;
    }

    public SendMessage getGamestartMessage(long chatId, long userId){
        SendMessage message = new SendMessage();
        message.setText("Начнем!");
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(menuService.getHintKeyboard());
        botStateCache.saveBotState(userId, BotState.SENDQUESTION);
        return message;
    }


    ////////////* QUIZ GAME EVENTS *////////////
    public SendMessage processAnswer(long chatId, long userId, boolean isCorrect) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (isCorrect) {
            message.setText("Правильно!");
            scoreCache.incrementScore(userId);
        }
        else
            message.setText("Ты пожалеешь об этой ошибке.");

        // see if this was the last question
        if (!questionDAO.exists(QuestionCache.getNextQuestionId(userId))) {
            Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
            telegramBot.execute(message);
            return processScoreEvent(chatId, userId);
        }
        message.setReplyMarkup(menuService.getNextQuestionKeyboard());
        return message;
    }

    ////////////* HINTS *////////////
    public BotApiMethod<?> processHintRequest(long chatId, long userId, Hint hint) throws TelegramApiException {
        HintCache.decreaseHint(userId, hint);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы выбрали подсказку \"" + HintConfig.getHintText(hint) + "\"." +
            "\nОсталось таких подсказок: " + HintCache.getRemainingHints(userId, hint));

        Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        telegramBot.execute(message);

        switch (hint) {
            case AUDIENCE_HELP:
                log.info("Audience help request received");
                break;
            case FIFTY_FIFTY:
                log.info("50/50 request received");
                return processFiftyFiftyRequest(chatId, userId, hint);
            case CALL_FRIEND:
                log.info("Call friend request received");
                break;
            default:
                log.info("Unknown hint request");
        }
        return null;
    }

    public SendMessage processFiftyFiftyRequest(long chatId, long userId, Hint hint) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        int questionId = QuestionCache.getCurrentQuestionId(userId);
        message.setReplyMarkup(menuService.getFiftyFiftyKeyboard(userId));
        message.setText("\uD83D\uDD39 " + questionDAO.findQuestionById(questionId).getText());

        return message;
    }

    // returns the next question message according to QuestionCache
    public SendMessage sendNextQuestion(long chatId, long userId) {
        QuestionCache.incrementQuestionId(userId);
        int questionId = QuestionCache.getCurrentQuestionId(userId);
        Question question = questionDAO.findQuestionById(questionId);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(menuService.getQuestionKeyboard(question.getAnswers()));
        message.setText("\uD83D\uDD39 " + question.getText());
        return message;
    }

    public SendMessage getDontGetDistracted(long chatId, long userId) {
        SendMessage message = new SendMessage();
        message.setText("Не будем отвлекаться.");
        message.setChatId(String.valueOf(chatId));
        return message;
    }

    ////////////* GAME END EVENTS *////////////
    public SendMessage processScoreEvent(long chatId, long userId){
        Score score = scoreCache.getCurrentScoreMap().get(userId);
        scoreCache.deleteScoreCache(userId);
        QuestionCache.deleteQuestionCache(userId);
        scoreDAO.saveScore(score);
        userDAO.saveScoreToUser(userId, score);
        return getScoreMessage(chatId, score.getPoints());
    }

    private SendMessage getScoreMessage(long chatId, int points){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вопросы закончены! Твой счет: " + points);
        message.setReplyMarkup(menuService.getTryAgainKeyboard());
        return message;
    }
}
