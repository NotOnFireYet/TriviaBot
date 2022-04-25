package com.software.triviabot.service;

import com.software.triviabot.data.User;
import com.software.triviabot.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserDAO {
    private final IUserRepo userRepo;

    @Autowired
    public UserDAO(IUserRepo userRepo){
        this.userRepo = userRepo;
    }

    public User findUserById(Long id){
        log.info("Fetching user with ID {}", id);
        return userRepo.getById(id);
    }

    public Boolean exists(Long id){
        log.info("Checking if user {} exists", id);
        User user = findUserById(id);
        return user != null;
    }

    public List<User> findAllUsers(){
        log.info("Fetching all users");
        return userRepo.findAll();
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
