package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService,
                          InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET enabled. List of films was successfully sent.");
        return inMemoryFilmStorage.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Integer filmId) {
        filmIdCheck(filmId);
        log.info("Film with id=" + filmId + " was successfully sent.");
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        log.info("List of prioritized films was successfully sent.");
        if (!filmService.getPopularFilms(count).isEmpty()) {
            return filmService.getPopularFilms(count).stream().
                    map(inMemoryFilmStorage::getFilmById).collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    @PutMapping
    public Film putFilm(@RequestBody @Valid Film film) {
        filmIdCheck(film.getId());
        log.info("Used PUT-method. Film with id " + film.getId() + " was replaced.");
        return inMemoryFilmStorage.putFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film setLikeToFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmAndUserIdCheck(userId, filmId);
        filmService.addLike(userId, filmId);
        log.info("Пользователь с id=" + userId + " поставл лайк фильму с id=" + filmId + ".");
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLikeFromFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmAndUserIdCheck(userId, filmId);
        filmService.removeLike(userId, filmId);
        log.info("Пользователь с id=" + userId + " убрал лайк с фильма с id=" + filmId + ".");
        return inMemoryFilmStorage.getFilmById(filmId);

    }

    @PostMapping
    public Film setFilm(@RequestBody @Valid Film film) {
        if (inMemoryFilmStorage.isThisFilmContained(film.getId())) {
            log.error("Film with this id is already added. Film was not be added.");
            throw new BeanAlreadyCreatedException("Film with id=" + film.getId() + " is already created.");
        }
        Film createdFilm = inMemoryFilmStorage.setFilm(film);
        filmService.addFilmId(createdFilm.getId());
        log.info("Used POST-method. Film with id " + film.getId() + " was added.");
        return createdFilm;
    }

    private void filmAndUserIdCheck(Integer userId, Integer filmId) {
        filmIdCheck(filmId);
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("Пользователь с id=" + userId + " не найден.");
            throw new BeanNotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    private void filmIdCheck(Integer filmId) {
        if (!inMemoryFilmStorage.isThisFilmContained(filmId)) {
            log.error("Фильм с id=" + filmId + " не найден.");
            throw new BeanNotFoundException("Фильм с id=" + filmId + " не найден.");
        }
    }

}