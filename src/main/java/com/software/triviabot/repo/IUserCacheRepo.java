package com.software.triviabot.repo;

import com.software.triviabot.model.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserCacheRepo extends JpaRepository<UserCache, Integer> {
}
