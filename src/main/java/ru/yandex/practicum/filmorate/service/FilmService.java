package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyCreatedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmsContract;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService implements FilmsContract {

    private final FilmDbStorage filmDao;
    private final GenreDbStorage genreDao;
    private final UserDbStorage userDao;
    private final LikesDbStorage likesDao;

    @Override
    public Film save(Film film) {
        try {
            filmDao.save(film);
            genreDao.setToFilm(film);
            log.info("Фильм film_id=" + film.getId() + " успешно добавлен.");
            return getFilmById(film.getId());
        } catch (NullPointerException e) {
            String message = "Ошибка при добавлении фильма film_id=" + film.getId() + " в базу данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Film update(Film film) {
        try {
            filmDao.update(film);
            genreDao.updateByFilm(film);
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
            list.forEach(e -> e.getGenres().addAll(genreDao.getByFilm(e.getId())));
            list.forEach(e -> e.getUsersIdWhoLikes().addAll(likesDao.getUserWhoLikesFilm(e.getId())));
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
            film.getGenres().addAll(genreDao.getByFilm(film.getId()));
            film.getUsersIdWhoLikes().addAll(likesDao.getUserWhoLikesFilm(film.getId()));
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
            likesDao.addLike(userId, filmId);//выкинет ошибку если лайк уже есть
            log.info("Пользователь user_id=" + userId + " поставил лайк фильму film_id=" + filmId + ".");
            return getFilmById(filmId);
        } catch (IncorrectResultSizeDataAccessException e) {
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
        likesDao.removeLike(userId, filmId);
        log.info("Пользователь user_id=" + userId + " убрал лайк с фильма film_id=" + filmId + ".");
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        try {
            List<Film> popularFilms = filmDao.getPopularFilms(limit);
            popularFilms.forEach(e -> e.getUsersIdWhoLikes().addAll(likesDao.getUserWhoLikesFilm(e.getId())));
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

}
