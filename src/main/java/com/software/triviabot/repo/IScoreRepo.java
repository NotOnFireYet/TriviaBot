package com.software.triviabot.repo;

import com.software.triviabot.data.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IScoreRepo extends JpaRepository<Score, Integer> {
}
