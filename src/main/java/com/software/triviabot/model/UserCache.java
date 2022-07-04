package com.software.triviabot.model;

import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name="user_cache")
// entity that saves all the game states to database
// upon shutdown
public class UserCache {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cache_id")
    private int cacheId;

    @Unique
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    private int fiftyFiftyRemains;

    private int audienceHelpRemains;

    private int callFriendRemains;

    private String state;
}
