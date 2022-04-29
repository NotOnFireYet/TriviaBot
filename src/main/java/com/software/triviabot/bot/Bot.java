package com.software.triviabot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
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

    private TelegramFacade telegramFacade;

    public Bot(TelegramFacade telegramFacade, DefaultBotOptions options,
        SetWebhook setWebhook, String botPath, String botUsername, String botToken) {
        super(options, setWebhook);
        this.telegramFacade = telegramFacade;
        this.botPath = botPath;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    public Bot(TelegramFacade telegramFacade, SetWebhook setWebhook) {
        super(setWebhook);
        this.telegramFacade = telegramFacade;
    }

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }
}
