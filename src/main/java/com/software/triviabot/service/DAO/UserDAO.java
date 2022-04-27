package com.software.triviabot.service.DAO;

import com.software.triviabot.data.Score;
import com.software.triviabot.data.User;
import com.software.triviabot.repo.IUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDAO {
    private final IUserRepo userRepo;
    private final EntityManager entityManager;

    public User findUserById(long userId){
        log.info("Fetching user with ID {}", userId);
        return userRepo.getById(userId);
    }

    public List<User> findAllUsers(){
        log.info("Fetching all users");
        return userRepo.findAll();
    }

    public Boolean exists(long userId){
        log.info("Checking if user {} exists", userId);
        List<?> resultSet = entityManager.createQuery(
            "SELECT u FROM User u WHERE user_id=" + userId)
            .getResultList();
        return resultSet.isEmpty() ? false : true;
    }

    public void saveScoreToUser(long userId, Score score){
        log.info("Saving score {} to user {}", score.getPoints(), userId);
        User user = userRepo.getById(userId);
        List<Score> newScores = user.getScores();
        newScores.add(score);
        user.setScores(newScores);
        userRepo.save(user);
    }

    public void saveUser(User user){
        log.info("Saving user {}", user.getUsername());
        userRepo.save(user);
    }

    public void deleteUser(User user){
        log.info("Deleting user {}", user.getUsername());
        userRepo.delete(user);
    }
}
