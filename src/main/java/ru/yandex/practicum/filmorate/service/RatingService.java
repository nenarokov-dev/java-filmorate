package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.RatingDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

@Service
@Slf4j
public class RatingService {

    private final RatingDaoImpl ratingDao;

    @Autowired
    public RatingService(RatingDaoImpl ratingDao) {
        this.ratingDao = ratingDao;
    }

    public List<FilmRating> getAllRatings() {
        return ratingDao.getAllRatings();
    }

    public FilmRating getRatingById(Integer ratingId) {
        try {
            return ratingDao.getRatingById(ratingId);
        } catch (EmptyResultDataAccessException e) {
            String message = "Не удалось получить рейтинг фильма rating_id=" + ratingId;
            log.error(message);
            throw new NotFoundException(message);
        }
    }
}
