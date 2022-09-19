package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.List;

public interface FilmsContract {

    Film setFilm(Film film) throws SQLException;

    Film putFilm(Film film) throws SQLException;

    Film getFilmById(Integer id) throws SQLException;

    List<Film> getAllFilms() throws SQLException;

    Film addLike(Integer userId, Integer filmId) throws SQLException;

    Film removeLike(Integer userId, Integer filmId) throws SQLException;

    List<Film> getPopularFilms(Integer counter) throws SQLException;

}
