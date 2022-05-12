package com.software.triviabot.repo.object;

import com.software.triviabot.data.Question;
import com.software.triviabot.data.Topic;
import com.software.triviabot.repo.ITopicRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicRepo {
    private final ITopicRepo topicRepo;

    public Topic findTopicById(int topicId){
        log.info("Fetching topic with ID {}", topicId);
        return topicRepo.getById(topicId);
    }

    public List<Topic> findAllTopics(){
        log.info("Fetching all topics");
        return topicRepo.findAll();
    }

    public void saveTopic(Topic topic){
        log.info("Saving topic {}", topic.getTopicId());
        for (Question question : topic.getQuestions()){
            question.setNumberInTopic(topic.getQuestions().indexOf(question) + 1);
        }
        topicRepo.save(topic);
    }
}
