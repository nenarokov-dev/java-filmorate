package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exception.AlreadyCreatedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmsContract;

import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class FilmService implements FilmsContract {

    private Integer filmsCounter;
    private final FilmDaoImpl filmDao;
    private final GenreDaoImpl genreDao;
    private final UserDaoImpl userDao;


    @Autowired
    public FilmService(FilmDaoImpl filmDao,
                       GenreDaoImpl genreDao, UserDaoImpl userDao) {
        this.filmDao = filmDao;
        this.genreDao = genreDao;
        this.userDao = userDao;
        this.filmsCounter = filmDao.getMaxFilmId();
    }

    @Override
    public Film setFilm(Film film) throws SQLException {
        try {
            if ((film.getId() == null) || (film.getId() <= 0)) {
                film.setId(generateId());
            } else {
                if (film.getId() > filmsCounter) {
                    filmsCounter = film.getId();
                }
            }
            filmDao.setFilm(film);
            genreDao.setGenreByFilm(film);
            log.info("Фильм film_id=" + film.getId() + " успешно добавлен.");
            return getFilmById(film.getId());
        } catch (SQLException e) {
            String message = "Ошибка при добавлении фильма film_id=" + film.getId() + " в базу данных.";
            log.error(message);
            throw new SQLException(message);
        } catch (NullPointerException e) {
            String message = "Ошибка при добавлении фильма film_id=" + film.getId() + " в базу данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Film putFilm(Film film) {
        try {
            filmDao.putFilm(film);
            genreDao.updateGenresByFilm(film);
            log.info("Фильм film_id=" + film.getId() + " успешно обновлён.");
            return getFilmById(film.getId());
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при обновлении фильма film_id=" + film.getId() + ". Фильм не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            List<Film> list = filmDao.getAllFilms();
            list.forEach(e -> e.getGenres().addAll(genreDao.getGenreByFilm(e.getId())));
            log.info("Cписок фильмов успешно получен");
            return list;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при получении списка всех фильмов из базы данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            Film film = filmDao.getFilmById(id);
            film.getGenres().addAll(genreDao.getGenreByFilm(film.getId()));
            log.info("Фильм film_id=" + id + " успешно получен.");
            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при получении фильма film_id=" + id + ".Фильм не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) {
        try {
            isUserWhoLikeExist(userId);//выкинет ошибку, если нет
            getFilmById(filmId);//так же выкинет ошибку, если фильм отсутствует в бд
            filmDao.addLike(userId, filmId);//выкинет SQLException если лайк уже есть
            log.info("Пользователь user_id=" + userId + " поставил лайк фильму film_id=" + filmId + ".");
            return getFilmById(filmId);
        } catch (SQLException e) {
            String message = "Пользователю user_id=" + userId + " не удалось поставить лайк фильму film_id=" + filmId +
                    ". Лайк фильму уже был поставлен.";
            log.error(message);
            throw new AlreadyCreatedException(message);
        }
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {

        isUserWhoLikeExist(userId);
        getFilmById(filmId);
        filmDao.removeLike(userId, filmId);
        log.info("Пользователь user_id=" + userId + " убрал лайк с фильма film_id=" + filmId + ".");
        return getFilmById(filmId);
    }//SQLException тут не будет, т.к. при удалении несуществующего лайка запрос успешно выполнится.

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        try {
            List<Film> popularFilms = filmDao.getPopularFilms(limit);
            log.info("Список приоритетных фильмов успешно получен");
            return popularFilms;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при получении списка всех фильмов из базы данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    private void isUserWhoLikeExist(Integer userId) {
        try {
            userDao.getUsersById(userId);
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    private Integer generateId() {
        return ++filmsCounter;
    }
}