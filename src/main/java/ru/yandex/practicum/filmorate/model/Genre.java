package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class Genre {
    private final Integer id;
    private final String name;
}


