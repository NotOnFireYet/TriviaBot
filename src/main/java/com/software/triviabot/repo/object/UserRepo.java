package com.software.triviabot.repo.object;

import com.software.triviabot.data.Score;
import com.software.triviabot.data.User;
import com.software.triviabot.repo.IUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRepo {
    private final IUserRepo userRepo;
    private final EntityManager entityManager;

    public User findUserById(long userId){
        log.info("Fetching user with ID {}", userId);
        return userRepo.getById(userId);
    }

    public Boolean exists(long userId){
        log.info("Checking if user {} exists", userId);
        return userRepo.existsById(userId);
    }

    public void saveScoreToUser(long userId, Score score){
        log.info("Saving score {} to user {}", score.getAnsweredQuestions(), userId);
        User user = userRepo.getById(userId);
        List<Score> newScores = user.getScores();
        newScores.add(score);
        user.setScores(newScores);
        userRepo.save(user);
    }

    public void saveNameToUser(long userId, String name){
        log.info("Saving name {} to user {}", name, userId);
        User user = userRepo.getById(userId);
        user.setName(name);
        userRepo.save(user);
    }

    public User saveUser(User user){
        log.info("Saving user {}", user.getUsername());
        return userRepo.save(user);
    }

    public User saveNewUser(long userId, String username){
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        return saveUser(user);
    }

    public void deleteUser(User user){
        log.info("Deleting user {}", user.getUsername());
        userRepo.delete(user);
    }
}
