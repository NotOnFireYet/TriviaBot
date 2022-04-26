package com.software.triviabot.service;

import com.software.triviabot.data.Answer;
import com.software.triviabot.service.DAO.UserDAO;
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
public class MenuService { // Constructs button layouts
    private UserDAO userDAO;

    @Value("${telegrambot.adminId}")
    private long adminId;

    public MenuService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /* BUILD START MESSAGE */
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

    /* BUILD BASE KEYBOARDS */
    private ReplyKeyboardMarkup getStartKeyboard(long userId){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Начать викторину"));
        keyboard.add(row1);

        // Shows admin panel for the admin user
        if (userId == adminId) {
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("Админская фигня"));
            keyboard.add(row2);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getHintKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("50/50"));
        row1.add(new KeyboardButton("Помощь зала"));
        row1.add(new KeyboardButton("Звонок другу"));
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    /* BUILD INLINE KEYBOARDS */
    public InlineKeyboardMarkup getQuestionKeyboard(List<Answer> answers) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        String isCorrect;

        // Compiles list of rows with buttons
        for (Answer answer : answers) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(answer.getText());

            // builds different callbacks for right and wrong answers
            isCorrect = answer.getIsCorrect() ? "Correct" : "Wrong";
            button.setCallbackData("answerCallback" + isCorrect);

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rowList.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNextQuestionKeyboard() {
        return getSingleButtonInlineKeyboard("Следующий вопрос", "nextQuestionCallback");
    }

    public InlineKeyboardMarkup getTryAgainKeyboard(){
        return getSingleButtonInlineKeyboard("Попробовать снова", "tryAgainCallback");
    }

    private InlineKeyboardMarkup getSingleButtonInlineKeyboard(String text, String callbackValue){
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
}
