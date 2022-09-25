package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film save(Film film);

    void update(Film film);

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer limit);

}
