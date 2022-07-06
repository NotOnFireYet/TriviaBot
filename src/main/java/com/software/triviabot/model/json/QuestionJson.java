package com.software.triviabot.model.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionJson {
    private String questionText;
    private List<String> answers;
    private String rightAnswer;
    private String rightAnswerReaction;
}
