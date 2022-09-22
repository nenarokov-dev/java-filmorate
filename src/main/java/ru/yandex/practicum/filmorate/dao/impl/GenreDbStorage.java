package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres", new GenreMapper());
    }

    @Override
    public Genre getById(Integer genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id=" + genreId;
        return jdbcTemplate.queryForObject(sql, new GenreMapper());
    }

    @Override
    public List<Genre> getByFilm(Integer filmId) {
        String sql = "SELECT gof.genre_id,g.genre \n" +
                "FROM genres_of_films AS gof\n" +
                "LEFT OUTER JOIN genres AS g ON gof.genre_id = g.genre_id\n" +
                "Where gof.film_id=" + filmId;
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public void setToFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO genres_of_films VALUES (?,?)", film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void updateByFilm(Film film) {
        deleteByFilm(film);
        setToFilm(film);
    }

    private void deleteByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM genres_of_films WHERE film_id=?", film.getId());
    }

}
