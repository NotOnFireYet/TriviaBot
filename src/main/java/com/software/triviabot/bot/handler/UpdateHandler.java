package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.model.Question;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.enums.State;
import com.software.triviabot.repo.object.UserRepo;
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
    private final UserRepo userRepo;

    private static final int nameLimit = 30;

    public BotApiMethod<?> handleUpdate(Update update) throws TelegramApiException {
        State state;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            state = StateCache.getState(callbackQuery.getFrom().getId());
            if (state == null || state == State.PREGAME) {
                return null;
            }
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                state = StateCache.getState(message.getFrom().getId());
                if ((state == null || state == State.PREGAME) && ! (message.getText().equals("/start") ||
                    message.getText().equals("Запустить"))) { // todo: doesn't work!!
                    return null;
                }
                return handleInputMessage(message);
            }
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) throws TelegramApiException, IllegalArgumentException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String input = message.getText();
        State state = StateCache.getState(userId);

        if (state == null) {
            if(input.equals("/start") || input.equals("Запустить"))
                return handleStartCommand(chatId, userId, message);
        } else {
            switch (state) {
                case START:         // ignore all user messages, including commands, before 1st game
                    return null;

                case ENTERNAME:
                    return handleEnteredName(chatId, userId, message);

                case FIRSTQUESTION:
                case GAMEPROCESS:
                    return handleGameProcessUpdate(chatId, userId, message);

                case GOTANSWER:
                case GIVEHINT:
                case DELETEALL: // delete all user messages, including commands
                    msgService.deleteUserMessage(chatId, message.getMessageId());
                    return null;

                case SCORE: // only react to main menu commands when main menu is displayed
                    return handleScoreMenuUpdate(chatId, userId, message);

                default:
                    throw new IllegalArgumentException("Unknown state:" + state);
            }
        }
        return null;
    }

    private BotApiMethod<?> handleGameProcessUpdate(long chatId, long userId, Message message) throws TelegramApiException {
        if (HintContainer.getAllHintTexts().contains(message.getText())) {
            StateCache.setState(userId, State.GIVEHINT);
            msgService.deleteUserMessage(chatId, message.getMessageId()); // delete hint request message for cleanliness
            Hint hint = HintContainer.getHintByText(message.getText());
            Question question = QuestionCache.getCurrentQuestion(userId);
            try {
                StateCache.setState(userId, State.GAMEPROCESS);
                HintCache.decreaseHint(userId, hint);
                switch (hint) {
                    case FIFTY_FIFTY:
                        eventHandler.processFiftyFiftyRequest(chatId, userId, question);
                        break;

                    case CALL_FRIEND:
                        eventHandler.processCallFriendRequest(chatId, userId, question);
                        break;

                    case AUDIENCE_HELP:
                        eventHandler.processAudienceHelpRequest(chatId, userId, question);
                        break;

                    default:
                        log.error("Unknown hint value: {}", hint.name());
                }
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
                eventHandler.handleNoMoreHints(chatId, userId);
            }
            return null;
        } else {
            return nonCommandHandler.handle(message); // if message isn't a hint request, pass to NonCommandHandler
        }
    }

    private BotApiMethod<?> handleStartCommand(long chatId, long userId, Message message) {
        if (userRepo.exists(userId)){
            StateCache.setState(userId, State.SCORE);
            return eventHandler.getWelcomeBackMessage(chatId, userId);
        } else {
            userRepo.saveNewUser(userId, message.getFrom().getUserName());
            StateCache.setState(userId, State.ENTERNAME); // to record next user message as name input
            return eventHandler.getIntroMessage(chatId);
        }
    }

    private BotApiMethod<?> handleEnteredName(long chatId, long userId, Message message) throws TelegramApiException {
        StateCache.setState(userId, State.START);
        String name = message.getText();
        if (name.length() > nameLimit){
            StateCache.setState(userId, State.ENTERNAME);
            return eventHandler.getNameTooLongMessage(chatId, nameLimit);
        }
        sender.send(eventHandler.processEnteredName(userId, chatId, message.getText()));
        sender.send(eventHandler.getRulesMessage(chatId));
        sendTopicOptions(chatId, userId);
        return null;
    }

    private BotApiMethod<?> handleScoreMenuUpdate(long chatId, long userId, Message message) throws TelegramApiException {
        String input = message.getText();
        switch (input) {
            case "Начать викторину":
                StateCache.setState(userId, State.FIRSTQUESTION);
                sendTopicOptions(chatId, userId);
                return null;

            case "Напомнить правила":
                return eventHandler.getRulesMessage(chatId);

            case "Моя статистика":
                return eventHandler.getStatsMessage(chatId, userId);

            case "Удалить мои данные":
                StateCache.setState(userId, State.DELETEALL);
                Message response = sender.send(eventHandler.getDeleteDataMessage(chatId));
                ActiveMessageCache.setDeleteMessage(userId, response);
                return null;

            default:
                return nonCommandHandler.handle(message);
        }
    }

    private void sendTopicOptions(long chatId, long userId) throws TelegramApiException {
        try {
            Message response = sender.send(eventHandler.getChooseTopicMessage(chatId, userId));
            ActiveMessageCache.setDeleteMessage(userId, response);
        } catch (NullPointerException | TelegramApiException e) {
            sender.send(eventHandler.getNoTopicsMessage(chatId));
            StateCache.setState(userId, State.PREGAME);
        }
    }
}
