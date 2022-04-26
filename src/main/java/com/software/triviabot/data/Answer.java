package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "answer")
public class Answer { // An entity for the answer, one-to-many with answers
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="answer_id")
    private long answerId;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    private String text;

    @Column(name="is_correct", nullable=false)
    private Boolean isCorrect; // marks the correct answer
}
