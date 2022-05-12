package com.software.triviabot;

import com.software.triviabot.data.Topic;
import com.software.triviabot.repo.object.TopicRepo;
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
    CommandLineRunner run(QuestionService questionService, TopicRepo topicRepo){
        return args -> {
            Topic topic1 = new Topic();
            topic1.setTitle("Пробная тема");
            topic1.setQuestions(Arrays.asList(
                questionService.createQuestion(topic1, "На каком языке написан этот бот?",
                    "Правильно! Этот бот написан на Java с огромным количеством классов.",
                    Arrays.asList("Python", "Java", "C#", "русский язык жестов"),
                    "Java"),

                questionService.createQuestion(topic1, "Сколько различают главных семейств динозавров?",
                    "Ответ верный! Динозавров разделяют на два основных семейства:" +
                        " ящеротазовые и птицетазовые.\n Как ни странно, птицы произошли от ящеротазовых.",
                    Arrays.asList("1", "2", "3", "4"),
                    "2"),

                questionService.createQuestion(topic1, "Понравилась ли вам эта викторина?",
                    "Верно! Если бы не понравилось, зачем забирать деньги?",
                    Arrays.asList("Очень", "В целом норм", "Не особо", "Худший опыт моей жизни"),
                    "Очень")
            ));
            topicRepo.saveTopic(topic1);

            Topic topic2 = new Topic();
            topic2.setTitle("Математика");
            topic2.setQuestions(Arrays.asList(
                questionService.createQuestion(topic2, "2 + 2 = ?",
                    "Правильно! Было бы странно, если бы вы этого не знали.",
                    Arrays.asList("1", "2", "3", "4"),
                    "4"),

                questionService.createQuestion(topic2, "5 + 5 = ?",
                    "Верно! Столько же, сколько пальцев на руке (в основном).",
                    Arrays.asList("20", "10", "-5", "???"),
                    "10"),

                questionService.createQuestion(topic2, "Какие были оценки по математике в школе?",
                    "Правильный ответ! Забирайте утешительный приз.",
                    Arrays.asList("Плохие", "Ну такие", "Хорошие", "Отличные"),
                    "Плохие")
            ));
            topicRepo.saveTopic(topic2);
        };
    }
}
