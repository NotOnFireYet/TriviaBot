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
            questionDAO.createQuestion("На каком языке написан этот бот?",
                "Правильно! Этот бот написан на Java с огромным количеством классов.",
                Arrays.asList("Python", "Java", "C#", "русский язык жестов"),
                "Java");
            questionDAO.createQuestion("Сколько различают главных семейств динозавров?",
                "Ответ верный! Динозавров разделяют на два основных семейства:" +
                    " ящеротазовые и птицетазовые.\n Как ни странно, птицы произошли от ящеротазовых.",
                Arrays.asList("1", "2", "3", "4"),
                "2");
            questionDAO.createQuestion("Понравилась ли вам эта викторина?",
                "Верно! Если бы тебе не понравилось, зачем забирать деньги?",
                Arrays.asList("Очень", "В целом норм", "Не особо", "Худший опыт моей жизни"),
                "Очень");
        };
    }
}
