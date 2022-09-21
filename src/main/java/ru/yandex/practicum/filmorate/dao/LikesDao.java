package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface LikesDao {

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    Set<Integer> getUserWhoLikesFilm(Integer filmId);
}
