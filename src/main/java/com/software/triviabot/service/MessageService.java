package com.software.triviabot.service;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {
    private final ReplySender sender;

    public void deleteCachedMessage(long chatId, long userId) throws TelegramApiException {
        int messageId = ActiveMessageCache.getDeleteMessage(userId).getMessageId();
        deleteUserMessage(chatId, messageId);
    }

    public void editMessageText(long chatId, long userId, String text) throws TelegramApiException {
        EditMessageText editText = new EditMessageText();
        editText.setChatId(String.valueOf(chatId));
        editText.setMessageId(ActiveMessageCache.getRefreshMessageId(userId));
        editText.setText(text);
        sender.edit(editText, null);
    }

    public void deleteUserMessage(long chatId, int messageId) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(String.valueOf(chatId));
        sender.delete(deleteMessage);
    }

    public void editInlineMarkup(long chatId, long userId, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        EditMessageReplyMarkup editKeyboard = new EditMessageReplyMarkup();
        editKeyboard.setChatId(String.valueOf(chatId));
        editKeyboard.setMessageId(ActiveMessageCache.getRefreshMessageId(userId));
        editKeyboard.setReplyMarkup(keyboard);
        sender.edit(null, editKeyboard);
    }

    public SendMessage buildMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.enableMarkdown(true);
        message.enableHtml(true);
        message.setText(text);
        return message;
    }
}
