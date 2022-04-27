package com.software.triviabot;

import com.software.triviabot.service.DAO.QuestionDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class TriviabotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TriviabotApplication.class, args);
	}

    @Bean
    CommandLineRunner run(QuestionDAO questionDAO){
        return args -> {
            questionDAO.buildAndSaveQuestion("На каком языке написан этот бот?",
                Arrays.asList("Python", "Java", "C#", "русский язык жестов"),
                "Java");
            questionDAO.buildAndSaveQuestion("Сколько различают главных семейств динозавров?",
                Arrays.asList("1", "2", "3", "4"),
                "2");
            questionDAO.buildAndSaveQuestion("Понравилась ли вам эта викторина?",
                Arrays.asList("Очень", "В целом норм", "Не особо", "Худший опыт моей жизни"),
                "Очень");
        };
    }
}
