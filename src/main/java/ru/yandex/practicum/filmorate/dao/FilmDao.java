package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.List;

public interface FilmDao {

    Film setFilm(Film film) throws SQLException;

    Film putFilm(Film film);

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    Film addLike(Integer userId, Integer filmId) throws SQLException;

    Film removeLike(Integer userId, Integer filmId);

    List<Film> getPopularFilms(Integer limit);

    Integer getMaxFilmId();

}