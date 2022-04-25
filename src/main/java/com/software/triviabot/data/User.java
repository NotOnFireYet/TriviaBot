package com.software.triviabot.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    private Long id;

    private String username;

    private int score;
}
