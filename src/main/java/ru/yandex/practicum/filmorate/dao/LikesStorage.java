package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface LikesStorage {

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    Set<Integer> getUserWhoLikesFilm(Integer filmId);
}
