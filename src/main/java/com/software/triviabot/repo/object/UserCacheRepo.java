package com.software.triviabot.repo.object;

import com.software.triviabot.model.UserCache;
import com.software.triviabot.repo.IUserCacheRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
// Keeps all the data to prevent from losing progress after app falling asleep
public class UserCacheRepo {
    private final IUserCacheRepo cacheRepo;
    private final EntityManager entityManager;

    public UserCache saveCache(UserCache cache) {
        log.info("Saving cache for user {}", cache.getUser().getUsername());
        return cacheRepo.save(cache);
    }

    public void deleteCache(UserCache cache) {
        log.info("Deleting cache for user {}", cache.getUser().getUsername());
        cacheRepo.delete(cache);
    }

    public UserCache findByUserId(long userId) {
        log.info("Getting cache for user {}", userId);
        List<UserCache> list = entityManager.createQuery(
                "SELECT c FROM UserCache c WHERE user_id=" + userId
                    + " ORDER BY RAND()")
            .getResultList();
        return !list.isEmpty() ? list.get(0) : null;
    }

    public UserCache getById(int id) {
        log.info("Fetching cache by id {} ", id);
        return cacheRepo.getById(id);
    }
}
