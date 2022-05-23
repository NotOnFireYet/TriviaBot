package com.software.triviabot.model;

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
    @JoinColumn(name="question_id")
    private Question question;

    @Column(length = 100)
    private String text;

    @Column(name="is_correct")
    private Boolean isCorrect;

    private int percentPicked; // for "audience help" hint

    @Override
    public String toString() {
        return "{answerId=" + this.getAnswerId() + ", questionId=" + this.question.getQuestionId()
            + ", text=" + this.text + ", isCorrect="  + this.isCorrect + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Answer answer = (Answer) o;
        if (!getQuestion().equals(answer.getQuestion()))
            return false;

        if (getIsCorrect() != answer.getIsCorrect()) {
            return false;
        }
        return getText() != null
            ? getText().equals(answer.getText())
            : answer.getText() == null;
    }
}
