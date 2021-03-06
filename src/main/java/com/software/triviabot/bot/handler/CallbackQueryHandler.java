package com.software.triviabot.bot.handler;

import com.software.triviabot.bot.ReplySender;
import com.software.triviabot.cache.ActiveMessageCache;
import com.software.triviabot.cache.HintCache;
import com.software.triviabot.cache.QuestionCache;
import com.software.triviabot.cache.StateCache;
import com.software.triviabot.model.*;
import com.software.triviabot.enums.State;
import com.software.triviabot.repo.object.*;
import com.software.triviabot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CallbackQueryHandler {
    private final TopicRepo topicRepo;
    private final AnswerRepo answerRepo;
    private final QuestionStatsRepo statRepo;
    private final UserRepo userRepo;

    private final EventHandler eventHandler;
    private final ReplySender sender;
    private final MessageService msgService;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws TelegramApiException {
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();
        String data = buttonQuery.getData();
        log.info("Received callback query: {}", data);

        if (data.endsWith("TopicCallback")) { // if user picked a topic
            msgService.deleteCachedMessage(chatId, userId);
            String topicIdString = data.replace("TopicCallback", "");
            int topicId = Integer.parseInt(topicIdString);
            Topic topic = topicRepo.findTopicById(topicId);
            QuestionCache.setUpCache(userId, topic);

            if (topic.getQuestions().isEmpty()) {
                eventHandler.handleNoQuestions(chatId);
                StateCache.setState(userId, State.START);
                Message response = sender.send(eventHandler.getChooseTopicMessage(chatId, userId));
                ActiveMessageCache.setDeleteMessageId(userId, response.getMessageId());
                return null;
            }

            HintCache.setUpCache(userId);
            sender.send(eventHandler.getKeyboardSwitchMessage(chatId));
            StateCache.setState(userId, State.FIRSTQUESTION);
            eventHandler.updateQuestion(chatId, userId);
            return null;
        }

        if (data.endsWith("AnswerCallback")) { // if user picked an answer
            Question question = QuestionCache.getCurrentQuestion(userId);
            String answerIdString = data.replace("AnswerCallback", "");
            int answerId = Integer.parseInt(answerIdString);
            Answer answer = answerRepo.findById(answerId);
            User user = userRepo.findUserById(userId);

            // if it's the first time this user answers this question
            if (!statRepo.hasStat(userId, question.getQuestionId())) {
                QuestionStat stat = new QuestionStat();
                stat.setQuestion(question);
                stat.setAnswer(answer);
                stat.setUser(user);
                statRepo.saveStat(stat); // record the answer for hint data
            }
            eventHandler.processAnswer(chatId, userId, answer.getIsCorrect());
            return null;
        }

        switch (data) {
            case "NextQuestionCallback":
                StateCache.setState(userId, State.GAMEPROCESS);
                eventHandler.updateQuestion(chatId, userId);
                break;

            case "ReplacementHintCallback":
                StateCache.setState(userId, State.GAMEPROCESS);
                Question question = QuestionCache.getCurrentQuestion(userId);
                eventHandler.processFiftyFiftyRequest(chatId, userId, question);
                break;

            case "NoHintsCallback":
                StateCache.setState(userId, State.GAMEPROCESS);
                QuestionCache.decreaseQuestionNum(userId);
                eventHandler.updateQuestion(chatId, userId);
                break;

            case "DeleteDataCallback":
                msgService.deleteCachedMessage(chatId, userId);
                eventHandler.deleteUserData(userId);
                return eventHandler.getGoodbyeMessage(chatId);

            case "GoBackCallback":
                StateCache.setState(userId, State.SCORE);
                msgService.deleteCachedMessage(chatId, userId);
                break;

            default:
                log.error("Unknown callback query");
                return null;
        }
        return null;
    }
}
