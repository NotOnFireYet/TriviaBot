package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.enums.State;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.StateCache;
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
public class NonCommandHandler {
    private final MessageService msgService;

    public BotApiMethod<?> handle(Message message) throws TelegramApiException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        State state = StateCache.getState(userId) == null ? State.IGNORE : StateCache.getState(userId);

        switch (state) {
            case GIVEHINT:
            case GAMEPROCESS:
                msgService.deleteUserMessage(chatId, message.getMessageId()); // delete all non-command messages
                break;

            case IGNORE:
            case START:
            case SCORE:
                return null; // ignore all non-command messages

            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
        return null;
    }
}
