package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.IsContainsSpase;

import javax.validation.constraints.*;

@Data
public class User {
    private int id;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Введённое значение не является почтой")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @IsContainsSpase(message = "Логин не должен содержать пробелов")
    private String login;
    private String name;
    @Max(value = 19211,message = "День рождения пользователя не должен быть в будущем")
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

    public User() {
    }

}