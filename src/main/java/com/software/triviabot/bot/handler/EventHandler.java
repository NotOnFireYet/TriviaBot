package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.container.FailMessageContainer;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.container.PriceContainer;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class EventHandler {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final ScoreDAO scoreDAO;
    private final MenuService menuService;
    private final ReplySender sender;

    ////////////* NEW USER START EVENTS */////////////
    public SendMessage processEnteredName(long userId, long chatId, String name) throws TelegramApiException {
        userDAO.saveNameToUser(userId, name);
        return getGreetingWithRules(chatId, name);
    }

    private SendMessage getGreetingWithRules(long chatId, String name) throws TelegramApiException {
        SendMessage message = buildMessage(chatId, "Здравствуй, " + name + "!");
        message.setReplyMarkup(menuService.getStartMenu());
        sender.send(message);
        return getRulesMessage(chatId);
    }

    public SendMessage getRulesMessage(long chatId){
        String rules = "Тебе предстоит 15 вопросов. " +
            "\nКаждый верный ответ увеличивает твой выигрыш, каждый неверный - " +
            "заканчивает игру.\nНо не все так просто!" +
            "\nТебе доступны подсказки:\n\n" +
            "<b>" + HintContainer.getHintText(Hint.FIFTY_FIFTY) + "</b>\n бот оставит 1 верный и 1 неверный ответ\n\n"+
            "<b>" + HintContainer.getHintText(Hint.CALL_FRIEND) + "</b>\n бот покажет, как на этот вопрос ответил другой рандомный пользователь " +
            "при первом прохождении\n\n" +
            "<b>" + HintContainer.getHintText(Hint.AUDIENCE_HELP) + "</b>\n бот покажет статистику ответов заранее опрошенной аудитории\n\n"+
            "Каждую подсказку можно использовать 2 раза за игру.\n" +
            "Желаем удачи!";
        return buildMessage(chatId, rules);
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

    public SendMessage getKeyboardSwitchMessage(long chatId){
        SendMessage message = buildMessage(chatId, "Начнем!");
        message.setReplyMarkup(menuService.getHintMenu());
        return message;
    }


    ////////////* QUIZ GAME EVENTS *////////////
    public void processAnswer(long chatId, long userId, boolean isCorrect) throws TelegramApiException {
        if (isCorrect) {
            int questionId = QuestionCache.getCurrentQuestionId(userId);
            String text = questionDAO.findQuestionById(questionId).getCorrectAnswerReaction();

            // see if this was the last question
            if (!questionDAO.exists(QuestionCache.getNextQuestionId(userId))) {
                editMessageText(chatId, text);
                processScoreEvent(chatId, userId, true);
                return;
            }
            text += "\n" + PriceContainer.getPriceByQuestionId(questionId) + " рублей твои!";
            editMessageText(chatId, text);
            editInlineMarkup(chatId, menuService.getNextQuestionKeyboard());
        } else
            processScoreEvent(chatId, userId, false);
    }

    ////////////* HINTS *////////////
    public void processHintRequest(long chatId, long userId, Hint hint) throws TelegramApiException {
        HintCache.decreaseHint(userId, hint);
        String text = "Ты выбрал подсказку \"" + HintContainer.getHintText(hint) + "\"." +
            "\nОсталось таких подсказок: " + HintCache.getRemainingHints(userId, hint);
        editMessageText(chatId, text);
        editInlineMarkup(chatId, menuService.getHintOkKeyboard(hint));
    }

    public void processCallFriendRequest(long chatId, long userId, int questionId) {
        log.info("Call Friend invoked.");
    }

    public void processAudienceHelpRequest(long chatId, long userId, int questionId) {
        log.info("Audience Help invoked.");
    }

    public void processFiftyFiftyRequest(long chatId, long userId, int questionId) throws TelegramApiException {
        String text = "\uD83D\uDD39 " + questionDAO.findQuestionById(questionId).getText();
        editMessageText(chatId, text);
        editInlineMarkup(chatId, menuService.getFiftyFiftyKeyboard(userId));
    }

    public void deleteUserMessage(long chatId, int messageId) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(String.valueOf(chatId));
        sender.delete(deleteMessage);
    }

    // returns the next question message according to QuestionCache
    public void sendNextQuestion(long chatId, long userId) throws TelegramApiException {
        QuestionCache.incrementQuestionId(userId);
        int questionId = QuestionCache.getCurrentQuestionId(userId);
        Question question = questionDAO.findQuestionById(questionId);
        String text = "\uD83D\uDD39 " + question.getText();
        InlineKeyboardMarkup keyboard = menuService.getQuestionKeyboard(question.getAnswers());

        // if first question to be sent, send as separate message
        if (BotStateCache.getCurrentState(userId) == BotState.GAMESTART){
            SendMessage message = buildMessage(chatId, text);
            message.setReplyMarkup(keyboard);
            ActiveMessageCache.setMessage(sender.send(message));
        } else {
            editMessageText(chatId, text);
            editInlineMarkup(chatId, keyboard);
        }
    }

    ////////////* GAME END EVENTS *////////////
    public void processScoreEvent(long chatId, long userId, boolean isSuccessful) throws TelegramApiException {
        BotStateCache.saveBotState(userId, BotState.SCORE);

        Score score = new Score();
        score.setUser(userDAO.findUserById(userId));
        score.setSuccessful(isSuccessful);

        int questionId = QuestionCache.getCurrentQuestionId(userId);
        score.setAnsweredQuestions(questionId);
        score.setGainedMoney(PriceContainer.getPriceByQuestionId(questionId));

        scoreDAO.saveScore(score);
        userDAO.saveScoreToUser(userId, score);

        getScoreMessage(chatId, userId, isSuccessful);

        SendMessage tryAgainMessage = buildMessage(chatId, "Ты можешь посмотреть свою статистику или попробовать еще раз.");
        tryAgainMessage.setReplyMarkup(menuService.getMainMenu());
        sender.send(tryAgainMessage);
    }

    private void getScoreMessage(long chatId, long userId, boolean isSuccessful) throws TelegramApiException {
        int lostMoney = 1000000 - PriceContainer.getPriceByQuestionId(QuestionCache.getCurrentQuestionId(userId));
        String text = isSuccessful ? "Вопросы закончены! Миллион рублей твои!" :
            "Увы, ответ неправильный! " +
                lostMoney + FailMessageContainer.getRandomFailMessage();
        editMessageText(chatId, text);
    }

    ////////////* MAINMENU *////////////
    public SendMessage getStatsMessage(long chatId, long userId) {
        int numOfTries = scoreDAO.findScoresByUserId(userId).size();
        int numOfWins = scoreDAO.getNumberOfWins(userId);
        long totalMoney = scoreDAO.getTotalMoney(userId);
        double winPercentage = numOfWins / (double)numOfTries * 100;

        User user = userDAO.findUserById(userId);
        String text = user.getName() + " (@" + user.getUsername() + "):"
            + "\n\n\uD83D\uDCC8<b> Всего игр:</b> " + numOfTries // graph emoji
            + "\n\n\uD83C\uDFC6<b> Побед:</b> " + numOfWins + "<i> (" + (int)winPercentage + "%)</i>" + // trophy emoji
            "\n\n\uD83D\uDCB8<b> Выиграно</b>: " + totalMoney + "р."; // money stack with wings emoji
        SendMessage message = buildMessage(chatId, text);
        return message;
    }

    ////////////* UTILITY *////////////

    private void editMessageText(long chatId, String text) throws TelegramApiException {
        EditMessageText editText = new EditMessageText();
        editText.setChatId(String.valueOf(chatId));
        editText.setMessageId(ActiveMessageCache.getMessageId());
        editText.setText(text);
        sender.edit(editText, null);
    }

    private void editInlineMarkup(long chatId, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        EditMessageReplyMarkup editKeyboard = new EditMessageReplyMarkup();
        editKeyboard.setChatId(String.valueOf(chatId));
        editKeyboard.setMessageId(ActiveMessageCache.getMessageId());
        editKeyboard.setReplyMarkup(keyboard);
        sender.edit(null, editKeyboard);
    }

    private SendMessage buildMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.enableMarkdown(true);
        message.enableHtml(true);
        message.setText(text);
        return message;
    }
}
