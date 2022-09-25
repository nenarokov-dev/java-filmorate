package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

public interface RatingStorage {

    List<FilmRating> getAll();

    FilmRating getById(Integer ratingId);

}
