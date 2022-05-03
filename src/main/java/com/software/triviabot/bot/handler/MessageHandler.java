package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.bot.enums.BotState;
import com.software.triviabot.bot.enums.Hint;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.service.DAO.UserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageHandler {
    private final UserDAO userDAO;
    private final EventHandler eventHandler;
    private final ReplySender sender;

    public BotApiMethod<?> handle(Message message, BotState botState) throws TelegramApiException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        BotStateCache.saveBotState(userId, botState);
        Message response;
        switch (botState) {
            case START:
                if (!userDAO.exists(userId))
                    eventHandler.saveNewUser(message.getFrom().getUserName(), userId);
                BotStateCache.saveBotState(userId, BotState.ENTERNAME);
                response = sender.send(eventHandler.getStartMessage(chatId)); // sends the greeting
                ActiveMessageCache.setMessage(response); // sets the greeting as the message it's gonna be editing
                break;

            case ENTERNAME:
                sender.send(eventHandler.processEnteredName(userId, chatId, message.getText()));
                BotStateCache.saveBotState(userId, BotState.IGNORE);
                break;

            case GAMESTART:
                sender.send(eventHandler.getKeyboardSwitchMessage(chatId));
                eventHandler.sendNextQuestion(chatId, userId);
                BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
                break;

            case SENDQUESTION: // if user sends typed message during active quiz game
                eventHandler.deleteUserMessage(chatId, message.getMessageId());
                break;

            case GIVEHINT:
                eventHandler.deleteUserMessage(chatId, message.getMessageId()); // delete hint request message for cleanliness
                Hint hint = HintContainer.getHintByText(message.getText());
                eventHandler.processHintRequest(chatId, userId, hint);
                break;

            case REMINDRULES:
                BotStateCache.saveBotState(userId, BotState.SCORE);
                return eventHandler.getRulesMessage(chatId);

            case GETSTATS:
                BotStateCache.saveBotState(userId, BotState.SCORE);
                return eventHandler.getStatsMessage(chatId, userId);

            case IGNORE:
                return null;

            default:
                throw new IllegalStateException("Unknown bot state: " + botState);
        }
        return null;
    }
}
