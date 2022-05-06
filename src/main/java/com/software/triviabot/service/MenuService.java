package com.software.triviabot.service;

import com.software.triviabot.data.Topic;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.service.DAO.QuestionDAO;
import com.software.triviabot.service.DAO.TopicDAO;
import com.software.triviabot.service.DAO.UserDAO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter @Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuService { // Constructs button layouts
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;
    private final TopicDAO topicDAO;

    //////////* START KEYBOARDS *//////////
    public ReplyKeyboardMarkup getMainMenu(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        row1.add(new KeyboardButton("Начать викторину"));
        row2.add(new KeyboardButton("Моя статистика"));
        row3.add(new KeyboardButton("Напомнить правила"));
        row3.add(new KeyboardButton("Удалить мои данные"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getTopicsMenu(){
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Topic topic : topicDAO.findAllTopics()){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(topic.getTitle());
            button.setCallbackData(topic.getTopicId() + "TopicCallback");
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rowList.add(row);
        }
        inlineMarkup.setKeyboard(rowList);
        return inlineMarkup;
    }


    //////////* HINT KEYBOARDS *//////////
    public ReplyKeyboardMarkup getHintMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        // create and set text to all the hint buttons
        for (Hint hint : Hint.values()){
            KeyboardButton button = new KeyboardButton();
            button.setText(HintContainer.getHintText(hint));
            row.add(button);
        }
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    // leaves one correct and one wrong answer
    public InlineKeyboardMarkup getFiftyFiftyKeyboard(long userId, List<Answer> answers) {
        int i = 0;
        while (answers.size() > 2) {
            if (!answers.get(i).getIsCorrect())
                answers.remove(i);
            i++;
        }
        return getQuestionKeyboard(answers);
    }


    public InlineKeyboardMarkup getHintOkKeyboard(Hint hint){
        return getOneButtonInlineKeyboard("Понятно", hint.name() + "_Ok");
    }


    //////////* QUESTION KEYBOARDS *//////////
    public InlineKeyboardMarkup getQuestionKeyboard(List<Answer> answers) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        String isCorrect;
        for (Answer answer : answers) { // Compiles list of rows with buttons
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(answer.getText());
            isCorrect = answer.getIsCorrect() ? "Correct" : "Wrong"; // builds callbacks for right and wrong answers
            button.setCallbackData("answerCallback" + isCorrect);

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rowList.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNextQuestionKeyboard() {
        return getOneButtonInlineKeyboard("Следующий вопрос", "nextQuestionCallback");
    }

    //////////* UTILITY *//////////
    private InlineKeyboardMarkup getOneButtonInlineKeyboard(String text, String callbackValue){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackValue);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getOneButtonMenu(String text){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(text));
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
