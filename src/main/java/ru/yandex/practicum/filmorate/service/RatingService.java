package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.RatingDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.interfaces.RatingContract;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RatingService implements RatingContract {

    private final RatingDbStorage ratingDao;

    public List<FilmRating> getAll() {
        return ratingDao.getAll();
    }

    public FilmRating getById(Integer ratingId) {
        try {
            return ratingDao.getById(ratingId);
        } catch (EmptyResultDataAccessException e) {
            String message = "Не удалось получить рейтинг фильма rating_id=" + ratingId;
            log.error(message);
            throw new NotFoundException(message);
        }
    }
}
