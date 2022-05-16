package com.software.triviabot.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
@Table(name = "scores")
// Entity for the user score to keep statistics
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
