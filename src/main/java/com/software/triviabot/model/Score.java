package com.software.triviabot.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "scores")
// entity for the user score after each game
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="score_id")
    private int scoreId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private int answeredQuestions;

    private boolean isSuccessful;

    private int gainedMoney;
}
