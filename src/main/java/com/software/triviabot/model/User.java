package com.software.triviabot.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name="users")
public class User { // user entity, binds to telegram account by user id
    @Id
    @NotNull
    @Column(name="user_id")
    private long userId;

    private String username;

    private String name;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
    private List<Score> scores;

    @Override
    public String toString() {
        return "{userId=" + this.getUserId() + ", username=" + this.getUsername() + ", name="
            + this.getName() + "}";
    }
}
