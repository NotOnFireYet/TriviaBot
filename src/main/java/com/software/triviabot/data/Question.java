package com.software.triviabot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @NotNull
    @Column(unique=true)
    private String text;

    private String correctAnswerReaction; // todo: implement reactions

    private String wrongAnswerReaction;

    @Column(unique=true)
    @OneToMany(mappedBy="question", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Answer> answers;
}
