package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.dao.mappers.IntegerMapper;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer userId, Integer filmId) {
        jdbcTemplate.update("INSERT INTO likes VALUES (?,?)", filmId, userId);
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public Set<Integer> getUserWhoLikesFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id=" + filmId,
                new IntegerMapper("user_id")));
    }

}
