package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping
    public Film putFilm(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film setLikeToFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLikeFromFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return filmService.removeLike(userId, filmId);
    }

    @PostMapping
    public Film setFilm(@RequestBody @Valid Film film) {
        return filmService.save(film);
    }

}
