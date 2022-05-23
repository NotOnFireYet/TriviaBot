package com.software.triviabot.model;

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
public class User {
    @Id
    @NotNull
    @Column(name="user_id")
    private long userId;

    private String username;

    private String name;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
    private List<Score> scores;
}
