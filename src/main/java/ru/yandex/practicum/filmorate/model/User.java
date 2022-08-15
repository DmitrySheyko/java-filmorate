package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {
    private static int identificator = 0;
    private int id = setId();
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotBlank
    private String birthday;

    public int setId() {
        return ++identificator;
    }
}


