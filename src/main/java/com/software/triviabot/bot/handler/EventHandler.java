package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.data.Answer;
import com.software.triviabot.enums.State;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.container.FailMessageContainer;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.container.PriceContainer;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.Score;
import com.software.triviabot.data.User;
import com.software.triviabot.service.DAO.ScoreDAO;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EventHandler {
    private final UserDAO userDAO;
    private final ScoreDAO scoreDAO;

    private final MenuService menuService;
    private final ReplySender sender;
    private final MessageService msgService;

    ////////////* NEW USER START EVENTS */////////////
    public SendMessage processEnteredName(long userId, long chatId, String name) {
        userDAO.saveNameToUser(userId, name);
        return msgService.buildMessage(chatId, "Здравствуйте, " + name + "!");
    }

    public SendMessage getInvalidNameMessage(long chatId){
        return msgService.buildMessage(chatId, "Имя должно содержать текст!");
    }

    public SendMessage getChooseTopicMessage(long chatId) {
        SendMessage message = msgService.buildMessage(chatId, "Выберите тему, чтобы начать викторину.");
        message.setReplyMarkup(menuService.getTopicsMenu());
        return message;
    }

    public SendMessage getRulesMessage(long chatId){
        String rules = "Вам предстоит 15 вопросов. " +
            "\nКаждый верный ответ увеличивает ваш выигрыш, каждый неверный - " +
            "заканчивает игру.\nНо не все так просто!" +
            "\nВам доступны подсказки:\n\n" +
            "<b>" + HintContainer.getText(Hint.FIFTY_FIFTY) +
            "</b>\n бот оставит 1 верный и 1 неверный ответ\n\n"+
            "<b>" + HintContainer.getText(Hint.CALL_FRIEND) +
            "</b>\n бот покажет, как на этот вопрос ответил другой рандомный пользователь " +
            "при первом прохождении\n\n" +
            "<b>" + HintContainer.getText(Hint.AUDIENCE_HELP) +
            "</b>\n бот покажет статистику ответов заранее опрошенной аудитории\n\n"+
            "Каждую подсказку можно использовать 2 раза за игру.\n" +
            "Желаем удачи!";
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


    ////////////* QUIZ GAME EVENTS *////////////

    // updates question message according to QuestionCache
    public void updateQuestion(long chatId, long userId) throws TelegramApiException {
        QuestionCache.incrementQuestionNum(userId);
        Question question = QuestionCache.getCurrentQuestion(userId);
        int num = QuestionCache.getCurrentQuestionNum(userId);
        String text = "№" + num + ". Вопрос на " +
            PriceContainer.getPriceByQuestionNum(num) + " рублей."
            + "\n\uD83D\uDD39 " + question.getText(); // blue diamond emoji
        InlineKeyboardMarkup keyboard = menuService.getQuestionKeyboard(question.getAnswers());
        int questionNum = QuestionCache.getCurrentQuestionNum(userId);

        if (questionNum == 1) { // if first question to be sent, send as separate message
            SendMessage message = msgService.buildMessage(chatId, text);
            message.setReplyMarkup(keyboard);
            ActiveMessageCache.setMessage(sender.send(message)); // set the message to be refreshed during quiz
        } else {
            msgService.editMessageText(chatId, text);
            msgService.editInlineMarkup(chatId, keyboard);
        }
    }

    public void processAnswer(long chatId, long userId, boolean isCorrect) throws TelegramApiException {
        if (isCorrect) {
            Question question = QuestionCache.getCurrentQuestion(userId);
            String text = question.getCorrectAnswerReaction();
            if (QuestionCache.isLastQuestion(userId)) {
                msgService.editMessageText(chatId, text);
                processScoreEvent(chatId, userId, true);
                return;
            }
            text += "\n" + PriceContainer.getPriceByQuestionNum(question.getNumberInTopic()) + " рублей ваши!";
            msgService.editMessageText(chatId, text);
            msgService.editInlineMarkup(chatId, menuService.getNextQuestionKeyboard());
        } else {
            processScoreEvent(chatId, userId, false);
        }
    }

    ////////////* HINTS *////////////
    public void processHintRequest(long chatId, long userId, Hint hint) throws TelegramApiException {
        HintCache.decreaseHint(userId, hint);
        String text = "Вы выбрали подсказку \"" + HintContainer.getText(hint) + "\"." +
            "\nОсталось таких подсказок: " + HintCache.getRemainingHints(userId, hint);
        msgService.editMessageText(chatId, text);
        msgService.editInlineMarkup(chatId, menuService.getHintOkKeyboard(hint));
    }

    public void handleNoMoreHints(long chatId) throws TelegramApiException {
        msgService.editMessageText(chatId, "Это подсказка закончилась :(");
        msgService.editInlineMarkup(chatId, menuService.getNoHintsOkKeyboard());
    }

    public void processCallFriendRequest(long chatId, long userId, Question question) {
        User user = userDAO.getRandomUser();
        log.info("Call Friend invoked.");
    }

    // todo: finish this
    public void processAudienceHelpRequest(long chatId, Question question) throws TelegramApiException {
        List<Answer> answers = new ArrayList<>(question.getAnswers());
        for (Answer answer : question.getAnswers()) { // edit answer texts to include percentages
            String answerText = answer.getText();
            answerText += " | " + answer.getPercentagePicked() + "%";
            answer.setText(answerText);
        }
        String questionText = "\uD83D\uDD39 " + question.getText();

        msgService.editMessageText(chatId, questionText);
        msgService.editInlineMarkup(chatId, menuService.getQuestionKeyboard(answers));

        for (Answer answer : answers) { // resetting answer texts
            String text = answer.getText();
            answer.setText(text.substring(0, text.indexOf(" ")));
        }
    }

    public void processFiftyFiftyRequest(long chatId, Question question) throws TelegramApiException {
        String text = "\uD83D\uDD39 " + question.getText();
        msgService.editMessageText(chatId, text);
        msgService.editInlineMarkup(chatId, menuService.getFiftyFiftyKeyboard(question.getAnswers()));
    }


    ////////////* GAME END EVENTS *////////////
    public void processScoreEvent(long chatId, long userId, boolean isSuccessful) throws TelegramApiException {
        StateCache.setState(userId, State.SCORE);

        Score score = new Score();
        score.setUser(userDAO.findUserById(userId));
        score.setSuccessful(isSuccessful);
        int questionNum = QuestionCache.getCurrentQuestionNum(userId);
        score.setAnsweredQuestions(questionNum);

        int questionPrice = 0;
        if (questionNum > 1) // if user came further than the 1st question
            questionPrice = isSuccessful ? PriceContainer.getPriceByQuestionNum(questionNum) :
                PriceContainer.getPriceByQuestionNum(questionNum - 1);
        score.setGainedMoney(questionPrice);
        scoreDAO.saveScore(score);
        userDAO.saveScoreToUser(userId, score);

        SendMessage tryAgainMessage = msgService.buildMessage(chatId, "Ваш выигрыш: " +
            questionPrice +" рублей. \nВы можете посмотреть свою статистику или попробовать еще раз.");
        tryAgainMessage.setReplyMarkup(menuService.getMainMenu());

        sendScoreMessage(chatId, questionPrice, isSuccessful);
        sender.send(tryAgainMessage);
    }

    private void sendScoreMessage(long chatId, int wonMoney, boolean isSuccessful) throws TelegramApiException {
        String text;
        if (isSuccessful){
            text = "Вопросы закончены! Миллион рублей ваши!";
            sender.send(msgService.buildMessage(chatId, text));
        } else {
            int lostMoney = 1000000 - wonMoney;
            text = "Увы, ответ неправильный! " + lostMoney + FailMessageContainer.getRandomFailMessage();
            msgService.editMessageText(chatId, text);
        }
    }


    ////////////* MAIN MENU *////////////
    public SendMessage getStatsMessage(long chatId, long userId) {
        int numOfTries = scoreDAO.findScoresByUserId(userId).size();
        int numOfWins = scoreDAO.getNumberOfWins(userId);
        long totalMoney = scoreDAO.getTotalMoney(userId);
        double winPercentage = numOfWins / (double)numOfTries * 100;

        User user = userDAO.findUserById(userId);
        String text = user.getName() + " | @" + user.getUsername()
            + "\n\n\uD83D\uDCC8<b> Всего игр:</b> " + numOfTries // graph emoji
            + "\n\n\uD83C\uDFC6<b> Побед:</b> " + numOfWins + "<i> (" + (int)winPercentage + "%)</i>" + // trophy emoji
            "\n\n\uD83D\uDCB8<b> Выиграно</b>: " + totalMoney + "р."; // money stack with wings emoji
        return msgService.buildMessage(chatId, text);
    }

    public SendMessage deleteUserData(long chatId, long userId) throws TelegramApiException {
        return msgService.buildMessage(chatId, "Функция в разработке"); // todo: this
    }
}
