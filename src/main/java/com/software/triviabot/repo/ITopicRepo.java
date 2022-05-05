package com.software.triviabot.repo;

import com.software.triviabot.data.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITopicRepo extends JpaRepository<Topic, Integer> {
}
