package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<FilmRating> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{ratingId}")
    public FilmRating getById(@PathVariable Integer ratingId) {
        return ratingService.getById(ratingId);
    }
}
