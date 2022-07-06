package com.software.triviabot.model;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "answers")
public class Answer { // entity for answers to quiz quiestions
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
        String res = "{answerId=" + getAnswerId();
        if (question != null) {
            res += ", questionId=" + question.getQuestionId();
        }
        res += ", text=" + text + ", isCorrect="  + isCorrect + "}";
        return res;
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
        return getText() != null ?
            getText().equals(answer.getText()) :
            answer.getText() == null;
    }
}
