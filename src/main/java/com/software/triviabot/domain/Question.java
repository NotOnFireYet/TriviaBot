package com.software.triviabot.domain;

import lombok.Data;

import java.util.HashMap;

// An entity for the game question
@Data
public class Question {
    private String text;
    private HashMap answers;
    private int answerKey;
}
