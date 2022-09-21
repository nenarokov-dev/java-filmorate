package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    List<Genre> getAll();

    Genre getById(Integer genreId);

    List<Genre> getGenresByFilm(Integer filmId);

    void setGenresToFilm(Film film);

    void updateGenresByFilm(Film film);

}
