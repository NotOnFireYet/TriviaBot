package com.software.triviabot.config;

import com.software.triviabot.bot.Bot;
import com.software.triviabot.bot.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppConfig {
    private final BotConfig botConfig;
    private final DbConfig dbConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, UpdateHandler updateHandler) {
        Bot bot = new Bot(updateHandler, setWebhook);
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotPath(botConfig.getWebHookPath());

        return bot;
    }
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(dbConfig.getDriverClassName());
        source.setUrl(dbConfig.getDbUrl());
        source.setUsername(dbConfig.getDbUsername());
        source.setPassword(dbConfig.getDbPassword());
        return source;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource());
        return namedParameterJdbcTemplate;
    }
}
