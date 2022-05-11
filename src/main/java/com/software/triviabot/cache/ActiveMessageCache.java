package com.software.triviabot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Service
public class ActiveMessageCache {
    // message to be edited during quiz.
    // whole quiz consists of refreshing this message
    private static Message currentMessage;

    // message with topic menu; cached to change its keyboard to hint menu
    // after user picks a topic
    private static Message topicMessage;

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

    public static Message getTopicMessage(){
        return topicMessage;
    }

    public static void setTopicMessage(Message message) {
        topicMessage = message;
    }

    private ActiveMessageCache(){}
}
