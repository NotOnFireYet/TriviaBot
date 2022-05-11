package com.software.triviabot.bot;

import com.software.triviabot.bot.handler.UpdateHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
@Setter
public class Bot extends SpringWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    private UpdateHandler updateHandler;

    public Bot(UpdateHandler updateHandler, DefaultBotOptions options,
        SetWebhook setWebhook, String botPath, String botUsername, String botToken) {
        super(options, setWebhook);
        this.updateHandler = updateHandler;
        this.botPath = botPath;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    public Bot(UpdateHandler updateHandler, SetWebhook setWebhook) {
        super(setWebhook);
        this.updateHandler = updateHandler;
    }

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateHandler.handleUpdate(update);
    }
}
