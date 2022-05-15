package com.software.triviabot.repo;

import com.software.triviabot.data.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITopicRepo extends JpaRepository<Topic, Integer> {
    @Override
    @Query("SELECT t FROM Topic t")
    List<Topic> findAll();
}
