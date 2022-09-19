package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.IntegerMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film setFilm(Film film) throws SQLException {
        jdbcTemplate.update("INSERT INTO films VALUES (?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
        return getFilmById(film.getId());//иначе вернётся фильм с null-name mpa
    }

    @Override
    public Film putFilm(Film film) {
        getFilmById(film.getId());//вернётся IncorrectResultSizeDataAccessException если фильма нет в базе.
        jdbcTemplate.update("UPDATE films SET name=?,description=?,release_date=?,duration=? WHERE film_id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        if (film.getMpa() != null) {
            jdbcTemplate.update("UPDATE films SET rating_id=? WHERE film_id=?", film.getMpa().getId(), film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = jdbcTemplate.queryForObject("SELECT f.film_id,f.name,f.description,f.release_date," +
                "f.duration,f.rating_id,r.rating FROM films AS f LEFT OUTER JOIN ratings AS " +
                "r ON f.rating_id=r.rating_id WHERE f.film_id=" + id, new FilmMapper());
        if (film != null) {//тут тоже по непонятной причине ворчит по поводу возможного вылета NullPointerException
            setUsersWhoLikes(film);
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films AS f LEFT OUTER JOIN ratings AS r ON " +
                "f.rating_id=r.rating_id", new FilmMapper());
        films.forEach(this::setUsersWhoLikes);
        return films;
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) throws SQLException {
        jdbcTemplate.update("INSERT INTO likes VALUES (?,?)", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        List<Film> filmsWithLikes = jdbcTemplate.query("Select film_id,count(user_id) as c from likes " +
                        "group by film_id ORDER BY c DESC LIMIT " + limit, new IntegerMapper("film_id"))
                .stream().map(this::getFilmById).collect(Collectors.toList());
        if (filmsWithLikes.size() < limit) {
            List<Film> otherFilmsWithoutLikesSortedById = jdbcTemplate.query(
                            "SELECT film_id FROM films WHERE film_id not in (select film_id from likes) LIMIT " +
                                    (limit - filmsWithLikes.size()), new IntegerMapper("film_id"))
                    .stream().map(this::getFilmById).collect(Collectors.toList());
            filmsWithLikes.addAll(otherFilmsWithoutLikesSortedById);
        }
        return filmsWithLikes;
    }

    private Set<Integer> getUserWhoLikesFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id=" + filmId,
                new IntegerMapper("user_id")));
    }

    public Integer getMaxFilmId() {
        return jdbcTemplate.query("SELECT max(film_id) AS max FROM films",
                new IntegerMapper("max")).stream().findAny().orElse(1);
    }

    private void setUsersWhoLikes(Film film) {
        film.getUsersIdWhoLikes().addAll(getUserWhoLikesFilm(film.getId()));
    }
}
