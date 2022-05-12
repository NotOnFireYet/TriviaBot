package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="question_id")
    private int questionId;

    @Column(nullable = false)
    private int numberInTopic;

    @ManyToOne
    @JoinColumn(name="topic_id")
    private Topic topic;

    @Column(unique=true, nullable = false)
    private String text;

    private String correctAnswerReaction;

    @Column(unique=true)
    @OneToMany(mappedBy="question", cascade = CascadeType.ALL,
        fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Answer> answers;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }

        Question q = (Question)o;
        return this.questionId == q.getQuestionId();
    }

    @Override
    public String toString() {
        return "{questionId=" + this.getQuestionId() + ", numberInTopic=" + this.numberInTopic +
            ", text=" + this.text + ", topicId="  + this.topic.getTopicId() + "}";
    }
}
