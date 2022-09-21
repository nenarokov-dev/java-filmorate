package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmsContract {

    Film save(Film film);

    Film update(Film film);

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    Film addLike(Integer userId, Integer filmId);

    Film removeLike(Integer userId, Integer filmId);

    List<Film> getPopularFilms(Integer counter);

}
