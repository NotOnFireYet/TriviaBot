package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.BotStateCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.data.Topic;
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
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();
        String data = buttonQuery.getData();
        log.info("Received callback query: {}", data);
        if (data.endsWith("TopicCallback")) {
            String topicIdString = data.replace("TopicCallback", "");
            int topicId = Integer.parseInt(topicIdString);
            QuestionCache.setTopic(userId, topicId);
            HintCache.setUpHints(userId);

            sender.send(eventHandler.getKeyboardSwitchMessage(chatId));
            BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
            eventHandler.updateQuestion(chatId, userId);
            return null;
        }

        // declaring it now bc question has been set in the TopicCallback
        Topic topic = topicDAO.findTopicById(QuestionCache.getCurrentTopicId(userId));
        int questionId = topic.getQuestions().get(QuestionCache.getCurrentQuestionNum(userId) - 1).getQuestionId();
        switch (data) {
            case ("answerCallbackCorrect"):
                BotStateCache.saveBotState(userId, BotState.GETANSWER);
                eventHandler.processAnswer(chatId, userId, true);
                break;

            case ("answerCallbackWrong"):
                BotStateCache.saveBotState(userId, BotState.GETANSWER);
                eventHandler.processAnswer(chatId, userId, false);
                break;

            case ("nextQuestionCallback"):
                BotStateCache.saveBotState(userId, BotState.SENDQUESTION);
                eventHandler.updateQuestion(chatId, userId);
                break;

            case ("FIFTY_FIFTY_Ok"):
                eventHandler.processFiftyFiftyRequest(chatId, userId, questionId);
                break;

            case ("CALL_FRIEND_Ok"):
                eventHandler.processCallFriendRequest(chatId, userId, questionId);
                break;

            case ("AUDIENCE_HELP_Ok"):
                eventHandler.processAudienceHelpRequest(chatId, userId, questionId);
                break;
        }


        return null;
    }
}
