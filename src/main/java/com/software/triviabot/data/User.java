package com.software.triviabot.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    @NotNull
    private long id;

    private String username;

    private int scores;
}
