package com.software.triviabot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
public class MenuService { //todo: this class is a mess. refactor repeating code
    private UserDAO userDAO;

    @Value("${telegrambot.adminId}")
    private int adminId;

    public MenuService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /* START MESSAGE */
    public SendMessage getStartingMessage(long chatId, long userId, String textMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getStartKeyboard(userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    private SendMessage createMessageWithKeyboard(long chatId, String textMessage,
        ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

    /* BUILD KEYBOARDS */
    private ReplyKeyboardMarkup getStartKeyboard(long userId){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Начать викторину"));
        keyboard.add(row);
        if (userId == adminId) {
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("Админская фигня"));
            keyboard.add(row2);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineQuestionKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton answer1 = new InlineKeyboardButton();
        answer1.setText("Вариант 1");
        InlineKeyboardButton answer2 = new InlineKeyboardButton();
        answer2.setText("Вариант 2");
        InlineKeyboardButton answer3 = new InlineKeyboardButton();
        answer3.setText("Вариант 3");
        InlineKeyboardButton answer4 = new InlineKeyboardButton();
        answer4.setText("Вариант 4");

        answer1.setCallbackData("answer1");
        answer2.setCallbackData("answer2");
        answer3.setCallbackData("answer3");
        answer4.setCallbackData("answer4");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(answer1);
        keyboardButtonsRow1.add(answer2);
        keyboardButtonsRow2.add(answer3);
        keyboardButtonsRow2.add(answer4);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getHintKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton("50/50"));
        row1.add(new KeyboardButton("Помощь зала"));
        row1.add(new KeyboardButton("Звонок другу"));

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
