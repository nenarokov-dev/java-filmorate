package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDaoImpl {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", new GenreMapper());
    }

    public Genre getGenreById(Integer genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id=" + genreId;
        return jdbcTemplate.queryForObject(sql, new GenreMapper());
    }

    public List<Genre> getGenreByFilm(Integer filmId) {
        String sql = "SELECT gof.genre_id,g.genre \n" +
                "FROM genres_of_films AS gof\n" +
                "LEFT OUTER JOIN genres AS g ON gof.genre_id = g.genre_id\n" +
                "Where gof.film_id=" + filmId;
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    public void setGenreByFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO genres_of_films VALUES (?,?)", film.getId(), genre.getId());
            }
        }
    }

    public void updateGenresByFilm(Film film) {
        deleteGenresByFilm(film);
        setGenreByFilm(film);
    }

    private void deleteGenresByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM genres_of_films WHERE film_id=?", film.getId());
    }
}
