package com.software.triviabot;

import com.software.triviabot.service.QuestionService;
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
    CommandLineRunner run(QuestionService questionService){
        return args -> {
            questionService.createQuestion("На каком языке написан этот бот?",
                Arrays.asList("Python", "Java", "C#", "русский язык жестов"),
                "Java");
            questionService.createQuestion("Сколько различают главных семейств динозавров?",
                Arrays.asList("1", "2", "3", "4"),
                "2");
            questionService.createQuestion("Понравилась ли вам эта викторина?",
                Arrays.asList("Очень", "В целом норм", "Не особо", "Худший опыт моей жизни"),
                "Очень");
        };
    }
}
