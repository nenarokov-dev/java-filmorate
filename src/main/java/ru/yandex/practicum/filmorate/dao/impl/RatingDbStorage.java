package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingStorage;
import ru.yandex.practicum.filmorate.dao.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

@Component
@AllArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<FilmRating> getAll() {
        return jdbcTemplate.query("SELECT * FROM ratings", new RatingMapper());
    }

    @Override
    public FilmRating getById(Integer ratingId) {
        String sql = "SELECT * from ratings WHERE rating_id=" + ratingId;
        return jdbcTemplate.queryForObject(sql, new RatingMapper());
    }

}
