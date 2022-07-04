package com.software.triviabot.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "questions")
public class Question { // entity for quiz questions
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="question_id")
    private int questionId;

    @ManyToOne
    @JoinColumn(name="topic_id")
    private Topic topic;

    @Column(unique=true, length = 500)
    private String text;

    @Column(unique=true, length = 500)
    private String correctAnswerReaction;

    private int numberInTopic;

    @Column(unique=true)
    @OneToMany(mappedBy="question", cascade = CascadeType.ALL,
        fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Answer> answers;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Question question = (Question) o;
        if (!getTopic().equals(question.getTopic()))
            return false;

        if (!getCorrectAnswerReaction().equals(question.getCorrectAnswerReaction())) {
            return false;
        }
        return getText() != null
            ? getText().equals(question.getText())
            : question.getText() == null;
    }

    @Override
    public String toString() {
        return "{questionId=" + this.getQuestionId() + ", text=" + this.text + ", topicId="  + this.topic.getTopicId() + "}";
    }
}
