package com.software.triviabot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ReplySender {
    public Message send(SendMessage message) throws TelegramApiException {
        Bot bot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        return bot.execute(message);
    }

    public void edit(EditMessageText text, EditMessageReplyMarkup keyboard) throws TelegramApiException {
        Bot bot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        if (text != null)
            bot.execute(text);
        if (keyboard != null)
            bot.execute(keyboard);
    }

    public void delete(DeleteMessage message) throws TelegramApiException {
        Bot bot = ApplicationContextProvider.getApplicationContext().getBean(Bot.class);
        bot.execute(message);
    }
}
