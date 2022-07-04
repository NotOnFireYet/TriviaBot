package com.software.triviabot.model;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "question_stats")
// entity for statistics to be used in
// "call friend" and "audience help" hints
public class QuestionStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stats_id")
    private int statsId;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
