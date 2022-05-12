package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "question_stats")
public class QuestionStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stats_id")
    private int statsId;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable=false)
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;
}
