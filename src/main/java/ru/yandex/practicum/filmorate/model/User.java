package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

@Data
public class User {
    private int id;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Введённое значение не является почтой")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^[0-9a-zA-Zа-яА-Я]$",message = "Логин не должен содержать пробелов")
    private String login;
    private String name;
    @Past(message = "День рождения пользователя не должен быть в будущем")
    private Long birthday;

    public User(String email, String login, String name, Long birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, Long birthday) {
        this.email = email;
        this.login = login;
        this.name = login;
        this.birthday = birthday;
    }

    public void setLogin(String login) {
        this.login = login;
        if (this.name.isBlank()) {
            this.name = login;
        }
    }

    public void setName(String name) {
        this.name = name;
        if (this.login.isBlank()) {
            this.login = name;
        }
    }
}