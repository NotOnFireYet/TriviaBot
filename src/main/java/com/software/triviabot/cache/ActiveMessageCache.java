package com.software.triviabot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

// Holds the currently edited message
@Slf4j
@Service
public class ActiveMessageCache {
    private static Message currentMessage;
    private static Message messageToDelete;

    public static void setMessage(Message message){
        currentMessage = message;
    }

    public static SendMessage getMessage(){
        SendMessage message = new SendMessage();
        message.setText(currentMessage.getText());
        if (currentMessage.getReplyMarkup() != null)
            message.setReplyMarkup(currentMessage.getReplyMarkup());
        return message;
    }

    public static int getMessageId(){
        return currentMessage.getMessageId();
    }

    public static Message getToDelete(){
        return messageToDelete;
    }

    public static void setToDelete(Message message) {
        messageToDelete = message;
    }

    private ActiveMessageCache(){}
}
