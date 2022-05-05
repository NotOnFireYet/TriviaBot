package com.software.triviabot;

import com.software.triviabot.data.Question;
import com.software.triviabot.data.Topic;
import com.software.triviabot.service.DAO.QuestionDAO;
import com.software.triviabot.service.DAO.TopicDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TriviabotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TriviabotApplication.class, args);
	}

    @Bean
    CommandLineRunner run(QuestionDAO questionDAO, TopicDAO topicDAO){
        return args -> {
            Topic topic1 = new Topic();
            topic1.setTitle("Пробная тема");
            topic1.setQuestions(Arrays.asList(
                questionDAO.createQuestion(topic1, "На каком языке написан этот бот?",
                    "Правильно! Этот бот написан на Java с огромным количеством классов.",
                    Arrays.asList("Python", "Java", "C#", "русский язык жестов"),
                    "Java"),

                questionDAO.createQuestion(topic1, "Сколько различают главных семейств динозавров?",
                    "Ответ верный! Динозавров разделяют на два основных семейства:" +
                        " ящеротазовые и птицетазовые.\n Как ни странно, птицы произошли от ящеротазовых.",
                    Arrays.asList("1", "2", "3", "4"),
                    "2"),

                questionDAO.createQuestion(topic1, "Понравилась ли вам эта викторина?",
                    "Верно! Если бы тебе не понравилось, зачем забирать деньги?",
                    Arrays.asList("Очень", "В целом норм", "Не особо", "Худший опыт моей жизни"),
                    "Очень")
            ));
            topicDAO.saveTopic(topic1);

            Topic topic2 = new Topic();
            topic2.setTitle("Математика");
            topic2.setQuestions(Arrays.asList(
                questionDAO.createQuestion(topic2, "2 + 2 = ?",
                    "Правильно! Было бы странно, если бы ты этого не знал.",
                    Arrays.asList("1", "2", "3", "4"),
                    "4"),

                questionDAO.createQuestion(topic2, "5 + 5 = ?",
                    "Верно! Столько же, сколько пальцев на руке. Наша дефолтная система исчисления.",
                    Arrays.asList("20", "10", "-5", "???"),
                    "10"),

                questionDAO.createQuestion(topic2, "Какие были оценки по математике в школе?",
                    "Правильный ответ! Не даем деньги привилегированным.",
                    Arrays.asList("Плохие", "Ну такие", "Хорошие", "Отличные"),
                    "Плохие")
            ));
            topicDAO.saveTopic(topic2);
        };
    }
}
