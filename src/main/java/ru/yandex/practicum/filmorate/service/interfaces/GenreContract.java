package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreContract {

    List<Genre> getAll();

    Genre getById(Integer genreId);
}
