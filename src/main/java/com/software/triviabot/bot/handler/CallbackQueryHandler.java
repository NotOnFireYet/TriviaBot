package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.data.Question;
import com.software.triviabot.enums.BotState;
import com.software.triviabot.service.DAO.TopicDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CallbackQueryHandler {
    private final EventHandler eventHandler;
    private final ReplySender sender;
    private final TopicDAO topicDAO;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws TelegramApiException {
        long chatId = buttonQuery.getMessage().getChatId(); // todo: stopped at ending game with new topic doesn't work
        long userId = buttonQuery.getFrom().getId();
        String data = buttonQuery.getData();
        log.info("Received callback query: {}", data);
        if (data.endsWith("TopicCallback")) {
            String topicIdString = data.replace("TopicCallback", "");
            int topicId = Integer.parseInt(topicIdString);
            QuestionCache.setupQuestionCache(userId, topicDAO.findTopicById(topicId));
            HintCache.setUpHints(userId);

            sender.send(eventHandler.getKeyboardSwitchMessage(chatId));
            BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
            eventHandler.updateQuestion(chatId, userId);
            return null;
        }

        // declaring it now bc question has been set in the TopicCallback
        Question question = QuestionCache.getCurrentQuestion(userId);
        log.info("Topic {}, question {}", QuestionCache.getCurrentTopic(userId).getTopicId(), QuestionCache.getCurrentQuestion(userId).getNumberInTopic());
        switch (data) {
            case ("answerCallbackCorrect"):
                eventHandler.processAnswer(chatId, userId, true);
                break;

            case ("answerCallbackWrong"):
                eventHandler.processAnswer(chatId, userId, false);
                break;

            case ("nextQuestionCallback"):
                BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
                eventHandler.updateQuestion(chatId, userId);
                break;

            case ("FIFTY_FIFTY_Ok"):
                eventHandler.processFiftyFiftyRequest(chatId, userId, question);
                break;

            case ("CALL_FRIEND_Ok"):
                eventHandler.processCallFriendRequest(chatId, userId, question);
                break;

            case ("AUDIENCE_HELP_Ok"):
                eventHandler.processAudienceHelpRequest(chatId, userId, question);
                break;
        }
        return null;
    }
}
