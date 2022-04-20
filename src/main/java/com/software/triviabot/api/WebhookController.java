package com.software.triviabot.api;

import com.software.triviabot.bot.Bot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {

    private final Bot telegramBot;

    public WebhookController(Bot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> processUpdate(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping
    public ResponseEntity respondToGetRequest() {
        return ResponseEntity.ok().build();
    }
}