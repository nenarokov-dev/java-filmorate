package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String saveMessage = "INSERT INTO films (name,description,release_date,duration,rating_id) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(saveMessage, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public void update(Film film) {
        jdbcTemplate.update("UPDATE films SET name=?,description=?,release_date=?,duration=?,rating_id=?" +
                        " WHERE film_id=?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        //работа с жанрами ведётся через GenreDaoImpl
    }//для методов контролера возвращаться будет getFilmById(id)

    @Override
    public Film getFilmById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT f.film_id,f.name,f.description,f.release_date," +
                "f.duration,f.rating_id,r.rating FROM films AS f LEFT OUTER JOIN ratings AS " +
                "r ON f.rating_id=r.rating_id WHERE f.film_id=" + id, new FilmMapper());
        //возвращает фильм с пустыми полями жанра и лайкнувших пользователей
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT f.film_id,f.name,f.description,f.release_date," +
                "f.duration,f.rating_id,r.rating FROM films AS f LEFT OUTER JOIN ratings AS " +
                "r ON f.rating_id=r.rating_id ", new FilmMapper());
        //возвращает фильмы с пустыми полями жанра и лайкнувших пользователей
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {

        String popularFilmsMessage = "Select f.film_id,f.name,f.description,f.release_date,f.duration," +
                "f.rating_id,r.rating,count(user_id) as c from likes as l " +
                "LEFT JOIN films as f ON l.film_id=f.film_id " +
                "LEFT JOIN ratings as r ON f.rating_id=r.rating_id " +
                "group by f.film_id,r.rating_id ORDER BY c DESC LIMIT " + limit;

        List<Film> filmsWithLikes = jdbcTemplate.query(popularFilmsMessage, new FilmMapper());

        if (filmsWithLikes.size() < limit) {
            List<Film> otherFilmsWithoutLikesSortedById = jdbcTemplate.query(
                    "Select f.film_id,f.name,f.description,f.release_date,f.duration,f.rating_id,r.rating\n" +
                            "from films as f LEFT JOIN ratings as r ON f.rating_id=r.rating_id WHERE film_id " +
                            "not in (select film_id from likes) LIMIT " +
                            (limit - filmsWithLikes.size()), new FilmMapper());
            filmsWithLikes.addAll(otherFilmsWithoutLikesSortedById);
        }
        return filmsWithLikes;
    }

}
