package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="answer_id")
    private int answerId;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    private String text;

    @Column(name="is_correct", nullable=false)
    private Boolean isCorrect;

    private int percentPicked; // for "audience help" hint

    @Override
    public String toString() {
        return "{answerId=" + this.getAnswerId() + ", questionId=" + this.question.getQuestionId()
            + ", text=" + this.text + ", isCorrect="  + this.isCorrect + "}";
    }
}
