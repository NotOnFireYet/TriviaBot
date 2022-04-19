package com.software.triviabot;

import com.software.triviabot.domain.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

@SpringBootApplication
public class TriviabotApplication {
	private static String TOKEN = "5372511255:AAFiJYn41uGi1X37BZIkDIzGrMwYcxRsJPY";
	private static final Map<String, String> getenv = System.getenv();

	public static void main(String[] args) {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new Bot(getenv.get("TriviaBot"), getenv.get(TOKEN)));
		} catch (TelegramApiException e){
			e.printStackTrace();
		}
	}

}
