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
@Table(name="users")
// Entity for the user, one-to-many with scores
public class User {
    @Id
    @NotNull
    @Column(name="user_id")
    private long userId;

    private String username;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Score> scores;
}
