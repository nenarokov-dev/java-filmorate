package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.dao.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class RatingDaoImpl {
    private final JdbcTemplate jdbcTemplate;

    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FilmRating> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM ratings", new RatingMapper());
    }

    public FilmRating getRatingOfFilm(Integer filmId) {
        String sql = "SELECT f.rating_id,r.rating \n" +
                "FROM films AS f \n" +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id = r.rating_id \n" +
                "Where f.film_id=" + filmId;
        return jdbcTemplate.queryForObject(sql, new RatingMapper());
    }

    public FilmRating getRatingById(Integer ratingId) {
        String sql = "SELECT * from ratings WHERE rating_id=" + ratingId;
        return jdbcTemplate.queryForObject(sql, new RatingMapper());
    }
}
