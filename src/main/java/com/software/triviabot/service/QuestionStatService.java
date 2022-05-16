package com.software.triviabot.service;

import com.software.triviabot.data.Answer;
import com.software.triviabot.data.Question;
import com.software.triviabot.data.QuestionStat;
import com.software.triviabot.repo.object.QuestionStatsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionStatService {
    private final QuestionStatsRepo statsRepo;
    private final int statLimit = 10;

    public List<Integer> getAnswerPercents(long userId, Question question){
        List<Answer> answers = new ArrayList<>(question.getAnswers());
        List<QuestionStat> stats = statsRepo.getAllStatsForQuestion(question.getQuestionId(), userId);
        Collections.shuffle(stats); // randomize stats
        List<QuestionStat> tenStats = stats // get up to 10 elements from stats
            .stream()
            .limit(statLimit)
            .collect(Collectors.toList());

        return !tenStats.isEmpty() ? calculatePercents(answers, tenStats) : new ArrayList<>();
    }

    private List<Integer> calculatePercents(List<Answer> answers, List<QuestionStat> stats) {
        List<Integer> percentages = new ArrayList<>();
        List<Answer> statAnswers = new ArrayList<>();
        stats.forEach(s -> statAnswers.add(s.getAnswer()));
        int sum = 0;
        for (Answer answer : answers){
            int frequency = Collections.frequency(statAnswers, answer);
            double result = (double)frequency / stats.size() * 100;
            result = result % 1 > 0.5 ? Math.ceil(result) : result; // if decimal part > 0.5, round up; else round down
            sum += (int)result;

            // if in the end the sum of percents != 100, adjust
            if (answers.indexOf(answer) == answers.size() - 1 && sum != 100){
                int difference = Math.abs(100 - sum);
                result = Math.abs(result - difference); // using abs in case result is 0
            }
            percentages.add((int)result);
        }
        return percentages;
    }
}
