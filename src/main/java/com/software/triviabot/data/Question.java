package com.software.triviabot.data;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

// An entity for the game question
@Entity
@Data
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(unique=true)
    private String text;

    @NotNull
    @Column(unique=true)
    private HashMap<Integer, String> answers;

    @NotNull
    private int answerKey; // key of the right answer
}
