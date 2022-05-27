package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.container.HintContainer;
import com.software.triviabot.container.PriceContainer;
import com.software.triviabot.enums.Hint;
import com.software.triviabot.enums.State;
import com.software.triviabot.model.Question;
import com.software.triviabot.model.UserCache;
import com.software.triviabot.repo.object.UserCacheRepo;
import com.software.triviabot.repo.object.UserRepo;
import com.software.triviabot.service.MenuService;
import com.software.triviabot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
    private final MenuService menuService;
    private final UserRepo userRepo;
    private final UserCacheRepo cacheRepo;

    private static final int nameLimit = 30;

    public BotApiMethod<?> handleUpdate(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null && message.hasText())
                return handleInputMessage(message);
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) throws TelegramApiException, IllegalArgumentException {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        if (!ActiveMessageCache.getChatIdMap().containsKey(userId))
            ActiveMessageCache.setChadId(userId, chatId);

        String input = message.getText();
        State state = StateCache.getState(userId);

        if (state == null) {
            if(input.equals("/start"))
                return handleStartCommand(chatId, userId, message);
            if (input.equals("Разбудить")) {
                if (userRepo.exists(userId))
                    return handleRebootRequest(chatId, userId);
                else
                    return handleStartCommand(chatId, userId, message);
            }
        } else {
            switch (state) {
                case ENTERNAME:
                    return handleEnteredName(chatId, userId, message);

                case FIRSTQUESTION:
                case GAMEPROCESS:
                    return handleGameProcessUpdate(chatId, userId, message);

                // delete all user messages, including commands, for the states below
                case START:
                case RIGHTANSWER:
                case DELETEDATA:
                    msgService.deleteMessage(chatId, message.getMessageId());
                    return null;

                case SCORE: // only react to main menu commands when main menu is displayed
                    return handleScoreMenuUpdate(chatId, userId, message);

                default:
                    throw new IllegalArgumentException("Unknown state:" + state);
            }
        }
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
                StateCache.setState(userId, State.DELETEDATA);
                Message response = sender.send(eventHandler.getDeleteDataMessage(chatId));
                ActiveMessageCache.setDeleteMessageId(userId, response.getMessageId());
                return null;

            default:
                return nonCommandHandler.handle(message);
        }
    }

    private BotApiMethod<?> handleGameProcessUpdate(long chatId, long userId, Message message) throws TelegramApiException {
        if (HintContainer.getAllHintTexts().contains(message.getText())) {
            msgService.deleteMessage(chatId, message.getMessageId()); // delete hint request message for cleanliness
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
        userRepo.saveNewUser(userId, message.getFrom().getUserName());
        StateCache.setState(userId, State.ENTERNAME); // to record next user message as name input
        return eventHandler.getIntroMessage(chatId);
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

    private BotApiMethod<?> handleRebootRequest(long chatId, long userId) throws TelegramApiException {
        UserCache cache = cacheRepo.existsByUserId(userId) ? cacheRepo.findByUserId(userId) : new UserCache();
        State state = cacheRepo.existsByUserId(userId) ? State.valueOf(cache.getState()) : State.SCORE;
        StateCache.setState(userId, state);
        Message response;
        switch (state) {
            case START:
                sendTopicOptions(chatId, userId);
                return null;

            case GAMEPROCESS:
            case FIRSTQUESTION:
                HintCache.extractFromCache(cache);
                QuestionCache.extractFromCache(userId, cache.getQuestion());
                QuestionCache.decreaseQuestionNum(userId); // bc question number auto-increases in updateQuestion()
                StateCache.setState(userId, State.FIRSTQUESTION); // to make it send as separate message
                sender.send(eventHandler.getWelcomeBackMessage(chatId, userId, menuService.getHintMenu()));
                eventHandler.updateQuestion(chatId, userId);
                return null;

            case RIGHTANSWER:
                HintCache.extractFromCache(cache);
                QuestionCache.extractFromCache(userId, cache.getQuestion());
                SendMessage message = eventHandler.getCorrectAnswerMessage(chatId, userId);
                sender.send(eventHandler.getWelcomeBackMessage(chatId, userId, menuService.getHintMenu()));
                response = sender.send(message);
                ActiveMessageCache.setRefreshMessageId(userId, response.getMessageId());
                return null;

            case SCORE:
                return eventHandler.getWelcomeBackMessage(chatId, userId, menuService.getMainMenu());

            case ENTERNAME:
                return eventHandler.getIntroMessage(chatId);

            case DELETEDATA:
                sender.send(eventHandler.getWelcomeBackMessage(chatId, userId, menuService.getMainMenu()));
                response = sender.send(eventHandler.getDeleteDataMessage(chatId));
                ActiveMessageCache.setDeleteMessageId(userId, response.getMessageId());
                return null;

            default:
                throw new IllegalArgumentException("Unknown state:" + state);
        }
    }

    private void sendTopicOptions(long chatId, long userId) throws TelegramApiException {
        try {
            Message response = sender.send(eventHandler.getChooseTopicMessage(chatId, userId));
            ActiveMessageCache.setDeleteMessageId(userId, response.getMessageId());
        } catch (NullPointerException | TelegramApiException e) {
            sender.send(eventHandler.getNoTopicsMessage(chatId));
            StateCache.setState(userId, State.PREGAME);
        }
    }
}
