package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

public interface RatingContract {

    List<FilmRating> getAll();

    FilmRating getById(Integer ratingId);

}
