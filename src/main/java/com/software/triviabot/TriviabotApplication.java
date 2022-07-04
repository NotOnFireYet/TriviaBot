package com.software.triviabot;

import com.software.triviabot.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class TriviabotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TriviabotApplication.class, args);
	}

	@Bean
	CommandLineRunner run(QuestionService questionService){ // populating db with topics & questions
		return args -> {
			questionService.generateQuizData();
		};
	}
}
