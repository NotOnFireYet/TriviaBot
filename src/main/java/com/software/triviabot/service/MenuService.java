package com.software.triviabot.service;

import com.software.triviabot.model.Topic;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.model.Answer;
import com.software.triviabot.repo.object.TopicRepo;
import com.software.triviabot.repo.object.UserRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
    private final UserRepo userRepo;
    private final QuestionService questionService;
    private final TopicRepo topicRepo;

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

    public ReplyKeyboard getDeleteOkKeyboard() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Продолжить");
        button1.setCallbackData("DeleteDataCallback");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Назад");
        button2.setCallbackData("GoBackCallback");

        row1.add(button1);
        row1.add(button2);

        rowList.add(row1);
        inlineMarkup.setKeyboard(rowList);
        return inlineMarkup;
    }

    public ReplyKeyboard getGoodbyeKeyboard() {
        return getOneButtonReplyKeyboard("Запустить");
    }

    public InlineKeyboardMarkup getTopicsMenu() throws NullPointerException {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<Topic> topics = topicRepo.findAllTopics();
        if (!topics.isEmpty()) {
            for (Topic topic : topics) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(topic.getTitle());
                button.setCallbackData(topic.getTopicId() + "TopicCallback");
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(button);
                rowList.add(row);
            }
            inlineMarkup.setKeyboard(rowList);
            return inlineMarkup;
        } else {
            throw new NullPointerException("No topics in database");
        }
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
            button.setText(HintContainer.getText(hint));
            row.add(button);
        }
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    // leaves one correct and one wrong answer
    public InlineKeyboardMarkup getFiftyFiftyKeyboard(List<Answer> answers) {
        int i = 0;
        while (answers.size() > 2) {
            if (!answers.get(i).getIsCorrect())
                answers.remove(i);
            i++;
        }
        return getQuestionKeyboard(answers);
    }

    public InlineKeyboardMarkup getNoHintsOkKeyboard() {
        return getOneButtonInlineKeyboard("Понятно", "NoHintsCallback");
    }

    public InlineKeyboardMarkup getReplacementHintOk(){
        return getOneButtonInlineKeyboard("Понятно", "ReplacementHintCallback");
    }


    //////////* QUESTION KEYBOARDS *//////////
    public InlineKeyboardMarkup getQuestionKeyboard(List<Answer> answers) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Answer answer : answers) { // Compiles list of rows with buttons
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(answer.getText());
            int answerId = answer.getAnswerId();
            button.setCallbackData(answerId + "AnswerCallback");

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rowList.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNextQuestionKeyboard() {
        return getOneButtonInlineKeyboard("Следующий вопрос", "NextQuestionCallback");
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

    private ReplyKeyboardMarkup getOneButtonReplyKeyboard(String text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(text));
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
