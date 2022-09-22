package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getAll();

    Genre getById(Integer genreId);

    List<Genre> getByFilm(Integer filmId);

    void setToFilm(Film film);

    void updateByFilm(Film film);

}
