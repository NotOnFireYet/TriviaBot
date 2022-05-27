package com.software.triviabot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name="user_cache")
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
