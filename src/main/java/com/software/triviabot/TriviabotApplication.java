package com.software.triviabot;

import com.software.triviabot.data.Topic;
import com.software.triviabot.repo.object.TopicRepo;
import com.software.triviabot.service.QuestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class TriviabotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TriviabotApplication.class, args);
	}
}
