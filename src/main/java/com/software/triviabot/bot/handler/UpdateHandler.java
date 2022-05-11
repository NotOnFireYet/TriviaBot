package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.enums.State;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.service.DAO.UserDAO;
import com.software.triviabot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateHandler {
    private final EventHandler eventHandler;
    private final NonCommandHandler nonCommandHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    private final ReplySender sender;
    private final MessageService msgService;
    private final UserDAO userDAO;

    public BotApiMethod<?> handleUpdate(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                return handleInputMessage(message);
            }
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) throws TelegramApiException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String input = message.getText();
        State state = StateCache.getState(userId);

        if (state == null && input.equals("/start")){
            StateCache.setState(userId, State.START);
            if (!userDAO.exists(userId)){
                userDAO.saveNewUser(userId, message.getFrom().getUserName());
            }
            StateCache.setState(userId, State.ENTERNAME); // to record next user message as name input
            return eventHandler.getIntroMessage(chatId);
        }

        Message response;
        if (state != null) {
            switch (state) {
                case START: // ignore all user messages, including commands, before 1st game
                    return null;

                case ENTERNAME:
                    if (message.getText().isEmpty())
                        return eventHandler.getInvalidNameMessage(chatId);
                    StateCache.setState(userId, State.START);
                    sender.send(eventHandler.processEnteredName(userId, chatId, message.getText()));
                    sender.send(eventHandler.getRulesMessage(chatId));
                    response = sender.send(eventHandler.getChooseTopicMessage(chatId));
                    ActiveMessageCache.setTopicMessage(response);
                    return null;

                case GAMEPROCESS:
                    if (HintContainer.getAllHintTexts().contains(input)) {
                        StateCache.setState(userId, State.GIVEHINT);
                        msgService.deleteUserMessage(chatId, message.getMessageId()); // delete hint request message for cleanliness
                        Hint hint = HintContainer.getHintByText(message.getText());
                        if (HintCache.getRemainingHints(userId, hint) < 1) { // if no hints of this type remain
                            eventHandler.handleNoMoreHints(chatId);
                            return null;
                        }
                        eventHandler.processHintRequest(chatId, userId, hint);
                        return null;
                    } else {
                        return nonCommandHandler.handle(message); // if message isn't a hint request, pass to NonCommandHandler
                    }

                case GIVEHINT: // delete all user messages, including commands, until hint process is over
                    msgService.deleteUserMessage(chatId, message.getMessageId());
                    return null;

                case SCORE: // only react to main menu commands when main menu is displayed
                    switch (input) {
                        case "Начать викторину":
                            StateCache.setState(userId, State.GAMEPROCESS);
                            response = sender.send(eventHandler.getChooseTopicMessage(chatId));
                            ActiveMessageCache.setTopicMessage(response);
                            return null;

                        case "Напомнить правила":
                            return eventHandler.getRulesMessage(chatId);

                        case "Моя статистика":
                            return eventHandler.getStatsMessage(chatId, userId);

                        case "Удалить мои данные":
                            return eventHandler.deleteUserData(chatId, userId);

                        default:
                            return nonCommandHandler.handle(message);
                    }

                default:
                    throw new IllegalArgumentException("Unknown state:" + state);
            }
        }

        return null;
    }
}
