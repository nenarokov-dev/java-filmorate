package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.IsContainsSpase;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @NotBlank(message = "Логин не должен быть пустым")
    @IsContainsSpase(message = "Логин не должен содержать пробелов")
    private String login;
    @NotNull
    private String name;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Введённая строка не обладает структурой email [***@**.**]")
    private String email;
    @Past(message = "День рождения пользователя должен быть в прошлом.")
    private LocalDate birthday;

    private final Set<Integer> friendsId = new HashSet<>();

}