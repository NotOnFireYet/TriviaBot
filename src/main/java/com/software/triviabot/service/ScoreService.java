package com.software.triviabot.service;

import com.software.triviabot.data.Score;
import com.software.triviabot.repo.object.ScoreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScoreService {
    private final ScoreRepo scoreRepo;

    public int getNumberOfWins(long userId){
        int result = 0;
        for (Score score : scoreRepo.findScoresByUserId(userId)){
            if(score.isSuccessful())
                result++;
        }
        return result;
    }

    public long getTotalMoney(long userId) {
        long result = 0;
        for (Score score : scoreRepo.findScoresByUserId(userId)){
            result += score.getGainedMoney();
        }
        return result;
    }
}
