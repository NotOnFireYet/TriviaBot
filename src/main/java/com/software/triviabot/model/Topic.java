package com.software.triviabot.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "topics")
public class Topic { // entity for quiz game topics
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private int topicId;

    @Column(length = 100)
    private String title;

    @OneToMany(mappedBy="topic", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Question> questions;

    public Question getQuestionByNumber(int num){
        for (Question q : this.questions) {
            if (q.getNumberInTopic() == num)
                return q;
        }
        return null;
    }

    public void addQuestion(Question q){
        questions.add(q);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Topic topic = (Topic) o;

        if (!getQuestions().equals(topic.getQuestions()))
            return false;

        return getTitle() != null ?
            getTitle().equals(topic.getTitle()) :
            topic.getTitle() == null;
    }
}
