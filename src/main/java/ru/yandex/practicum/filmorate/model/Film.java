package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.IsCorrectLocalData;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    private Integer id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;
    @IsCorrectLocalData(year = 1895, month = 12, day = 28,
            message = "Дата релиза не должна быть ранее 28 декабря 1895 года.")
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма должна быть положительной.")
    private Integer duration;
    @NotNull
    private FilmRating mpa;

    private final Set<Genre> genres = new HashSet<>();

    private final Set<Integer> usersIdWhoLikes = new HashSet<>();

}

