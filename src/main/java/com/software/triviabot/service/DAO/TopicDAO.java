package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Question;
import com.software.triviabot.data.Topic;
import com.software.triviabot.repo.ITopicRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicDAO {
    private final ITopicRepo topicRepo;
    private final QuestionDAO questionDAO;
    private final EntityManager entityManager;

    public Topic findTopicById(int topicId){
        log.info("Fetching topic with ID {}", topicId);
        return topicRepo.getById(topicId);
    }

    public void addQuestionToTopic(int topicId, Question question){
        Topic topic = topicRepo.getById(topicId);
        List<Question> questions = topic.getQuestions();
        if (topic.getQuestions() != null)
            questions.add(question);
        else
            questions = Arrays.asList(question);
        topic.setQuestions(questions);
        topicRepo.save(topic);
    }

    public List<Topic> findAllTopics(){
        log.info("Fetching all topics");
        return topicRepo.findAll();
    }

    public Boolean exists(int topicId){
        log.info("Checking if topic {} exists", topicId);
        return topicRepo.existsById(topicId);
    }

    public void saveTopic(Topic topic){
        log.info("Saving topic {}", topic.getTopicId());
        for (Question question : topic.getQuestions()){
            question.setNumberInTopic(topic.getQuestions().indexOf(question) + 1);
        }
        topicRepo.save(topic);
    }

    public void deleteTopic(Topic topic){
        log.info("Deleting topic {}", topic.getTitle());
        topicRepo.delete(topic);
    }
}
