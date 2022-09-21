package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.dao.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

@Component
public class RatingDaoImpl implements RatingDao {
    private final JdbcTemplate jdbcTemplate;

    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmRating> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM ratings", new RatingMapper());
    }

    @Override
    public FilmRating getRatingById(Integer ratingId) {
        String sql = "SELECT * from ratings WHERE rating_id=" + ratingId;
        return jdbcTemplate.queryForObject(sql, new RatingMapper());
    }
}
