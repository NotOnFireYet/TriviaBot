package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.container.FailMessageContainer;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.container.PriceContainer;
import com.software.triviabot.model.*;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.enums.State;
import com.software.triviabot.repo.object.QuestionStatsRepo;
import com.software.triviabot.repo.object.ScoreRepo;
import com.software.triviabot.repo.object.UserRepo;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.MessageService;
import com.software.triviabot.service.QuestionStatService;
import com.software.triviabot.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EventHandler {
    private final UserRepo userRepo;
    private final ScoreService scoreService;
    private final QuestionStatService statService;
    private final MenuService menuService;
    private final MessageService msgService;

    private final QuestionStatsRepo statRepo;
    private final ScoreRepo scoreRepo;

    private final ReplySender sender;

    private static final String questionEmoji = "\uD83D\uDD39 "; // blue diamond emoji

    ////////////* NEW USER START EVENTS */////////////
    public SendMessage processEnteredName(long userId, long chatId, String name) {
        userRepo.saveNameToUser(userId, name);
        return msgService.buildMessage(chatId, "Здравствуйте, " + name + "!");
    }

    public BotApiMethod<?> getNameTooLongMessage(long chatId, int nameLength) {
        return msgService.buildMessage(chatId, "Имя не должно превышать " + nameLength + " символов!");
    }

    public SendMessage getWelcomeBackMessage(long chatId, long userId) {
        User user = userRepo.findUserById(userId);
        return msgService.buildMessage(chatId, "С возвращением, " + user.getName() + "!");
    }

    public SendMessage getChooseTopicMessage(long chatId, long userId) throws NullPointerException, TelegramApiException {
        sendWishingLuckMessage(chatId, userId); // message that clears reply keyboard

        SendMessage message = msgService.buildMessage(chatId, "Выберите тему, чтобы начать викторину.");
        message.setReplyMarkup(menuService.getTopicsMenu());
        return message;
    }

    public void sendWishingLuckMessage(long chatId, long userId) throws TelegramApiException {
        List<Score> scores = scoreRepo.findScoresByUserId(userId);
        String text;
        if (!scores.isEmpty()) {
            boolean isLastWin = scores.get(scores.size() - 1).isSuccessful(); // see if the user's last try was a win
            text = isLastWin ? "За новыми победами!" : "В этот раз повезет ;)";
        } else {
            text = "Желаем удачи!";
        }
        SendMessage greetMessage = msgService.buildMessage(chatId, text);
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        greetMessage.setReplyMarkup(remove);
        sender.send(greetMessage);
    }

    public SendMessage getRulesMessage(long chatId){
        String rules = "Вам предстоит 15 вопросов. " +
            "\nКаждый верный ответ увеличивает выигрыш, каждый неверный - " +
            "заканчивает игру.\nНо не все так просто!" +
            "\nВам доступны подсказки:\n\n" +
            "<b>" + HintContainer.getText(Hint.FIFTY_FIFTY) +
            "</b>\n бот оставит 1 верный и 1 неверный ответ\n\n"+
            "<b>" + HintContainer.getText(Hint.CALL_FRIEND) +
            "</b>\n бот покажет, как на этот вопрос впервые ответил рандомный пользователь\n\n" +
            "<b>" + HintContainer.getText(Hint.AUDIENCE_HELP) +
            "</b>\n бот выберет 10 рандомных пользователей и покажет статистику их ответов на этот вопрос\n\n"+
            "Каждую подсказку можно использовать 1 раз за игру.";
        return msgService.buildMessage(chatId, rules);
    }

    public SendMessage getIntroMessage(long chatId) {
        String text = "Добрый день, дорогорй игрок! \n" +
            "Здесь вам предстоит попробовать свои силы в игре:" +
            " \"Кто хочет стать миллионером\"! \n" +
            "Пожалуйста, представьтесь:";
        return msgService.buildMessage(chatId, text);
    }

    public SendMessage getKeyboardSwitchMessage(long chatId){
        SendMessage message = msgService.buildMessage(chatId, "Начнем!");
        message.setReplyMarkup(menuService.getHintMenu());
        return message;
    }


    public void handleNoQuestions(long chatId) throws TelegramApiException {
        sender.send(msgService.buildMessage(chatId, "В теме нет вопросов. Пожалуйста, выберите другую."));
    }

    public SendMessage getNoTopicsMessage(long chatId) {
        return msgService.buildMessage(chatId, "Темы временно отсутствуют. Приносим извинения :(" +
            "\nПопробуйте позже (команда /start).");
    }


    ////////////* QUIZ GAME EVENTS *////////////

    // updates question message according to QuestionCache
    public void updateQuestion(long chatId, long userId) throws TelegramApiException {
        QuestionCache.incrementQuestionNum(userId);
        Question question = QuestionCache.getCurrentQuestion(userId);
        int num = QuestionCache.getCurrentQuestionNum(userId);
        String text = "№" + num + ". Вопрос на " +
            PriceContainer.getPriceByQuestionNum(num) + " рублей.\n"
            + questionEmoji + question.getText(); // blue diamond emoji
        InlineKeyboardMarkup keyboard = menuService.getQuestionKeyboard(question.getAnswers());

        if (StateCache.getState(userId) == State.FIRSTQUESTION) { // if first question to be sent, send as separate message
            SendMessage message = msgService.buildMessage(chatId, text);
            message.setReplyMarkup(keyboard);
            ActiveMessageCache.setRefreshMessage(userId, sender.send(message)); // set the message to be refreshed during quiz
        } else {
            msgService.editMessageText(chatId, userId, text);
            msgService.editInlineMarkup(chatId, userId, keyboard);
        }
    }

    public void processAnswer(long chatId, long userId, boolean isCorrect) throws TelegramApiException {
        if (isCorrect) {
            Question question = QuestionCache.getCurrentQuestion(userId);
            String text = question.getCorrectAnswerReaction();
            if (QuestionCache.isLastQuestion(userId)) {
                msgService.editMessageText(chatId, userId, text);
                processScoreEvent(chatId, userId, true);
                return;
            }
            text += "\n" + PriceContainer.getPriceByQuestionNum(QuestionCache.getCurrentQuestionNum(userId)) + " рублей ваши!";
            msgService.editMessageText(chatId, userId, text);
            msgService.editInlineMarkup(chatId, userId, menuService.getNextQuestionKeyboard());
        } else {
            processScoreEvent(chatId, userId, false);
        }
    }

    ////////////* HINTS *////////////
    public void handleNoMoreHints(long chatId, long userId) throws TelegramApiException {
        msgService.editMessageText(chatId, userId, "Это подсказка закончилась :(");
        msgService.editInlineMarkup(chatId, userId, menuService.getNoHintsOkKeyboard());
    }

    // seeing how another random user answered the same question
    public void processCallFriendRequest(long chatId, long userId, Question question) throws TelegramApiException {
        QuestionStat stat = statRepo.getRandomByQuestionId(question.getQuestionId());
        if (stat == null){
            editNoHintDataMessage(chatId, userId);
        } else {
            List<Answer> answers = question.getAnswers();
            for (Answer answer : answers) {
                if (answer.equals(stat.getAnswer()))
                    answer.setText(answer.getText() + " | выбор друга");
            }
            String text = questionEmoji + question.getText();
            InlineKeyboardMarkup keyboard =  menuService.getQuestionKeyboard(question.getAnswers());
            msgService.editMessageText(chatId, userId, text);
            msgService.editInlineMarkup(chatId, userId, keyboard);
            resetAnswerTexts(answers);
        }
    }

    public void processAudienceHelpRequest(long chatId, long userId, Question question) throws TelegramApiException {
        List<Answer> answers = question.getAnswers();
        List<Integer> percents = statService.getAnswerPercents(userId, question);
        if (!percents.isEmpty()){
            appendPercentsToAnswers(answers, percents);
            String text = questionEmoji + question.getText();
            InlineKeyboardMarkup keyboard = menuService.getQuestionKeyboard(answers);
            msgService.editMessageText(chatId, userId, text);
            msgService.editInlineMarkup(chatId, userId, keyboard);
            resetAnswerTexts(answers);
        } else {
            editNoHintDataMessage(chatId, userId);
        }
    }

    public void processFiftyFiftyRequest(long chatId, long userId, Question question) throws TelegramApiException {
        String text = questionEmoji + question.getText();
        msgService.editMessageText(chatId, userId, text);
        msgService.editInlineMarkup(chatId, userId, menuService.getFiftyFiftyKeyboard(question.getAnswers()));
    }


    ////////////* GAME END EVENTS *////////////
    public void processScoreEvent(long chatId, long userId, boolean isSuccessful) throws TelegramApiException {
        StateCache.setState(userId, State.SCORE);

        Score score = new Score();
        score.setUser(userRepo.findUserById(userId));
        score.setSuccessful(isSuccessful);
        int questionNum = QuestionCache.getCurrentQuestionNum(userId);
        score.setAnsweredQuestions(questionNum);

        int questionPrice = 0;
        if (questionNum > 1) // if user came further than the 1st question
            questionPrice = isSuccessful ? PriceContainer.getPriceByQuestionNum(questionNum) :
                PriceContainer.getPriceByQuestionNum(questionNum - 1);
        score.setGainedMoney(questionPrice);
        scoreRepo.saveScore(score);
        userRepo.saveScoreToUser(userId, score);

        SendMessage tryAgainMessage = msgService.buildMessage(chatId, "Ваш выигрыш: " +
            questionPrice +" рублей. \nВы можете посмотреть свою статистику или попробовать еще раз.");
        tryAgainMessage.setReplyMarkup(menuService.getMainMenu());

        sendScoreMessage(chatId, userId, questionPrice, isSuccessful);
        sender.send(tryAgainMessage);
    }

    public void sendScoreMessage(long chatId, long userId, int wonMoney, boolean isSuccessful) throws TelegramApiException {
        String text;
        if (isSuccessful){
            text = "Вопросы закончены! Поздравляем с победой - миллион рублей ваши \uD83C\uDF89"; // party hat emoji
            sender.send(msgService.buildMessage(chatId, text));
        } else {
            int lostMoney = 1000000 - wonMoney;
            text = "Увы, ответ неправильный! " + lostMoney + FailMessageContainer.getRandomFailMessage();
            msgService.editMessageText(chatId, userId, text);
        }
    }


    ////////////* MAIN MENU *////////////
    public SendMessage getStatsMessage(long chatId, long userId) {
        int numOfTries = scoreRepo.findScoresByUserId(userId).size();
        int numOfWins = scoreService.getNumberOfWins(userId);
        long totalMoney = scoreService.getTotalMoney(userId);
        double winPercentage = numOfWins / (double)numOfTries * 100;

        User user = userRepo.findUserById(userId);
        String text = user.getName() + " | @" + user.getUsername()
            + "\n\n\uD83D\uDCC8<b> Всего игр:</b> " + numOfTries // graph emoji
            + "\n\n\uD83C\uDFC6<b> Побед:</b> " + numOfWins + "<i> (" + (int)winPercentage + "%)</i>" + // trophy emoji
            "\n\n\uD83D\uDCB8<b> Выиграно</b>: " + totalMoney + "р."; // money stack with wings emoji
        return msgService.buildMessage(chatId, text);
    }

    public SendMessage getDeleteDataMessage(long chatId) {
        SendMessage message = msgService.buildMessage(chatId, "Бот забудет ваш юзернейм, имя и статистику.");
        message.setReplyMarkup(menuService.getDeleteOkKeyboard());
        return message;
    }

    public void deleteUserData(long userId) {
        ActiveMessageCache.clearCache(userId);
        QuestionCache.clearCache(userId);
        HintCache.clearCache(userId);
        StateCache.clearCache(userId);
        scoreRepo.deleteUserScores(userId);
        statRepo.deleteAllUserStats(userId);
        userRepo.deleteUser(userRepo.findUserById(userId));
    }

    ////////////* UTILITY *////////////

    private void editNoHintDataMessage(long chatId, long userId) throws TelegramApiException {
        String text = "Удивительно, но вы первыми столкнулись с этим вопросом. Поздравляем? \uD83E\uDD28" + //raised eyebrow emoji
            "\nВы увидите подсказку \"50/50\" за счет этой.";
        InlineKeyboardMarkup keyboard = menuService.getReplacementHintOk();
        msgService.editMessageText(chatId, userId, text);
        msgService.editInlineMarkup(chatId, userId, keyboard);
    }

    private void appendPercentsToAnswers(List<Answer> answers, List<Integer> percents){
        for (int i = 0; i < answers.size(); i++)
            answers.get(i).setPercentPicked(percents.get(i));

        for (Answer answer : answers) { // edit answer texts to include percentages
            String answerText = answer.getText();
            answerText += " | " + answer.getPercentPicked() + "%";
            answer.setText(answerText);
        }
    }

    private void resetAnswerTexts(List<Answer> answers) {
        for (Answer answer : answers) {
            String answerText = answer.getText();
            if (answerText.contains("|"))
                answer.setText(answerText.substring(0, answerText.indexOf("|")));
        }
    }
}
