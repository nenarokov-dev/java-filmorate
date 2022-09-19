package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService,FilmService filmService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<FilmRating> getAllFilmRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{ratingId}")
    public FilmRating getRatingOfFilm(@PathVariable Integer ratingId) {
        return ratingService.getRatingById(ratingId);
    }
}