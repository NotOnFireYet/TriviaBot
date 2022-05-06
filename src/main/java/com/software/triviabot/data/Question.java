package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "questions")
// Question entity, one-to-many with answers
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
}
