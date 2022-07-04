package com.software.triviabot.model.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionJsonWrapper {
    private String questionText;
    private List<String> answers;
    private String rightAnswer;
    private String rightAnswerReaction;
}
