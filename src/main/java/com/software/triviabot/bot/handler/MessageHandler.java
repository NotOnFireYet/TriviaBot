package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.data.User;
import com.software.triviabot.enums.BotState;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MessageService;
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
    private final MessageService msgService;

    public BotApiMethod<?> handle(Message message, BotState botState) throws TelegramApiException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        BotStateCache.saveBotState(userId, botState);
        Message response;
        switch (botState) {
            case START:
                if (!userDAO.exists(userId)){
                    User user = new User();
                    user.setUserId(userId);
                    user.setUsername(message.getFrom().getUserName());
                    userDAO.saveUser(user);
                }
                BotStateCache.saveBotState(userId, BotState.ENTERNAME);
                return eventHandler.getStartMessage(chatId); // sends the greeting

            case ENTERNAME:
                if (message.getText().isEmpty()){
                    eventHandler.getInvalidNameMessage(chatId);
                    break;
                }
                BotStateCache.saveBotState(userId, BotState.IGNORE);
                sender.send(eventHandler.processEnteredName(userId, chatId, message.getText()));
                sender.send(eventHandler.getRulesMessage(chatId));

                response = sender.send(eventHandler.getChooseTopicMessage(chatId));
                ActiveMessageCache.setMessage(response);
                break;

            case GAMESTART:
                BotStateCache.saveBotState(userId, BotState.IGNORE); // ignore user's messages while topic menu is displayed
                return eventHandler.getChooseTopicMessage(chatId);

            case SENDQUESTION:
                msgService.deleteUserMessage(chatId, message.getMessageId());
                break;

            case SENDQUESTION_AFTER_HINT:
                msgService.deleteUserMessage(chatId, message.getMessageId());
                eventHandler.handleDoubleHintRequest(message.getChatId());
                break;

            case GIVEHINT:
                msgService.deleteUserMessage(chatId, message.getMessageId()); // delete hint request message for cleanliness
                Hint hint = HintContainer.getHintByText(message.getText());
                if (HintCache.getRemainingHints(userId, hint) < 1){ // if out of hints
                    eventHandler.handleNoMoreHints(chatId);
                    break;
                }
                eventHandler.processHintRequest(chatId, userId, hint);
                BotStateCache.saveBotState(userId, BotState.IGNORE);
                break;

            case REMINDRULES:
                BotStateCache.saveBotState(userId, BotState.IGNORE);
                return eventHandler.getRulesMessage(chatId);

            case GETSTATS:
                BotStateCache.saveBotState(userId, BotState.IGNORE);
                return eventHandler.getStatsMessage(chatId, userId);

            case DELETEDATA:
                return eventHandler.deleteUserData(chatId, userId);

            case IGNORE:
                return null;

            default:
                throw new IllegalStateException("Unknown bot state: " + botState);
        }
        return null;
    }
}
