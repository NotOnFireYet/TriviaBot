package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ApplicationContextProvider;
import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.container.FailMessageContainer;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.container.QuestionPriceContainer;
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

    private final BotStateCache botStateCache;

    ////////////* NEW USER START EVENTS */////////////
    public SendMessage processEnteredName(long userId, long chatId, String name) throws TelegramApiException {
        userDAO.saveNameToUser(userId, name);
        return getGreetingWithRules(chatId, name);
    }

    private SendMessage getGreetingWithRules(long chatId, String name) throws TelegramApiException {
        Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        telegramBot.execute(getSimpleMessage(chatId, "Здравствуй, " + name + "!"));;
        return getRulesMessage(chatId);
    }

    public SendMessage getRulesMessage(long chatId){
        String rules = "Тебе предстоит 15 вопросов. " +
            "\nКаждый верный ответ увеличивает твой выигрыш, каждый неверный - " +
            "полностью его обнуляет.\nНо не все так просто!" +
            "\nТебе доступны подсказки:\n\n" +
            "<b>" + HintContainer.getHintText(Hint.FIFTY_FIFTY) + "</b>\n бот оставит 1 верный и 1 неверный ответ\n\n"+
            "<b>" + HintContainer.getHintText(Hint.CALL_FRIEND) + "</b>\n бот покажет, как на этот вопрос ответил другой рандомный пользователь " +
            "при первом прохождении\n\n" +
            "<b>" + HintContainer.getHintText(Hint.AUDIENCE_HELP) + "</b>\n бот покажет статистику ответов заранее опрошенной аудитории\n\n"+
            "Каждую подсказку можно использовать 2 раза за игру.\n" +
            "Желаем удачи!";
        SendMessage message = getSimpleMessage(chatId, rules);
        message.enableHtml(true);
        message.setReplyMarkup(menuService.getStartKeyboard());
        return message;
    }

    private SendMessage getSimpleMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
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
        int questionId = QuestionCache.getCurrentQuestionId(userId);
        String correctMessage = questionDAO.findQuestionById(questionId).getCorrectAnswerReaction();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (isCorrect) {
            String text = correctMessage;
            if (questionId != questionDAO.getNumberOfQuestions())
                text += "\n" + QuestionPriceContainer.getPriceByQuestionId(questionId) + " рублей твои!";
            message.setText(text);

            // see if this was the last question
            if (!questionDAO.exists(QuestionCache.getNextQuestionId(userId))) {
                BotStateCache.saveBotState(userId, BotState.SCORE);
                Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
                telegramBot.execute(message);
                return processScoreEvent(chatId, userId, true);
            }
            message.setReplyMarkup(menuService.getNextQuestionKeyboard());
            return message;
        } else
            return processScoreEvent(chatId, userId, false);
    }

    ////////////* HINTS *////////////
    public BotApiMethod<?> processHintRequest(long chatId, long userId, Hint hint) throws TelegramApiException {
        HintCache.decreaseHint(userId, hint);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Ты выбрал подсказку \"" + HintContainer.getHintText(hint) + "\"." +
            "\nОсталось таких подсказок: " + HintCache.getRemainingHints(userId, hint));

        Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        telegramBot.execute(message);

        int questionId = QuestionCache.getCurrentQuestionId(userId);
        switch (hint) {
            case AUDIENCE_HELP:
                log.info("Audience help request received");
                break;
            case FIFTY_FIFTY:
                log.info("50/50 request received");
                return processFiftyFiftyRequest(chatId, userId, questionId);
            case CALL_FRIEND:
                log.info("Call friend request received");
                return processCallFriendRequest(chatId, userId, questionId);
            default:
                log.info("Unknown hint request"); // todo: throw exception
        }
        return null;
    }

    public SendMessage processCallFriendRequest(long chatId, long userId, int questionId) {
        return null;
    }

    public SendMessage processFiftyFiftyRequest(long chatId, long userId, int questionId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
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
        String name = userDAO.findUserById(userId).getName();
        SendMessage message = new SendMessage();
        message.setText(name + ", не будем отвлекаться.");
        message.setChatId(String.valueOf(chatId));
        return message;
    }

    ////////////* GAME END EVENTS *////////////
    public SendMessage processScoreEvent(long chatId, long userId, boolean isSuccessful) throws TelegramApiException {
        Score score = new Score();
        score.setUser(userDAO.findUserById(userId));
        score.setSuccessful(isSuccessful);
        int questionId = QuestionCache.getCurrentQuestionId(userId);
        score.setAnsweredQuestions(questionId);
        score.setTotalMoney(QuestionPriceContainer.getPriceByQuestionId(questionId));
        scoreDAO.saveScore(score);
        userDAO.saveScoreToUser(userId, score);

        Bot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        telegramBot.execute(getScoreMessage(chatId, userId, isSuccessful));

        return getSimpleMessage(chatId, "Ты можешь посмотреть свою статистику или попробовать еще раз.");
    }

    private SendMessage getScoreMessage(long chatId, long userId, boolean isSuccessful) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (isSuccessful){
            message.setText("Вопросы закончены! Миллион рублей твои!");
        } else {
            message.setText("Увы, ответ неправильный! " +
                QuestionPriceContainer.getPriceByQuestionId(QuestionCache.getCurrentQuestionId(userId)) +
                    FailMessageContainer.getRandomFailMessage());
            message.setChatId(String.valueOf(chatId));
        }
        message.setReplyMarkup(menuService.getMainKeyboard());
        return message;
    }
}
