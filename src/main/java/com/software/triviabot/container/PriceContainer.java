package com.software.triviabot.container;

import com.google.common.collect.ImmutableMap;
import com.software.triviabot.bot.enums.Hint;
import org.springframework.stereotype.Component;

import java.util.Map;

// keeps prices corresponding to each question number
// for easier price and question editing
@Component
public class PriceContainer {
    private static final Map<Integer, Integer> questionPriceMap = ImmutableMap.<Integer, Integer>builder()
        .put(1, 100)
        .put(2, 200)
        .put(3, 300)
        .put(4, 500)
        .put(5, 1000)
        .put(6, 2000)
        .put(7, 4000)
        .put(8, 8000)
        .put(9, 16000)
        .put(10, 32000)
        .put(11, 64000)
        .put(12, 125000)
        .put(13, 250000)
        .put(14, 500000)
        .put(15, 1000000)
        .build();

    public static Integer getPriceByQuestionId(int questionId){
        return questionPriceMap.get(questionId);
    }

    public static Integer getQuestionIdByPrice(int price){
        for (Map.Entry<Integer, Integer> entry : questionPriceMap.entrySet()) {
            if (entry.getValue().equals(price)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
