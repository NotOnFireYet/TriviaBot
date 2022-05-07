package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "scores")
// Entity for the scores to keep statistics.
// Many-to-one with users
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
