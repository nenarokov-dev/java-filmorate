package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
    public List<Genre> getGenresByFilm(Integer filmId) {
        String sql = "SELECT gof.genre_id,g.genre \n" +
                "FROM genres_of_films AS gof\n" +
                "LEFT OUTER JOIN genres AS g ON gof.genre_id = g.genre_id\n" +
                "Where gof.film_id=" + filmId;
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public void setGenresToFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO genres_of_films VALUES (?,?)", film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void updateGenresByFilm(Film film) {
        deleteGenresByFilm(film);
        setGenresToFilm(film);
    }

    private void deleteGenresByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM genres_of_films WHERE film_id=?", film.getId());
    }

}
