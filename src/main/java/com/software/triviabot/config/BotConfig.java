package com.software.triviabot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {
    @Value("${telegrambot.webHookPath}")
    private String webHookPath;

    @Value("${telegrambot.userName}")
    private String userName;

    @Value("${telegrambot.botToken}")
    private String botToken;

    @Value("${telegrambot.adminId}")
    private Long adminId;
}
