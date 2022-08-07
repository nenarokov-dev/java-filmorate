package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> filmStorage = new HashMap<>();
    private final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting();
    private final Gson gson = gsonBuilder.create();

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET enabled. List of films was successfully sent.");
        return new ArrayList<>(filmStorage.values());
    }

    @PutMapping("/film")
    public String putFilm(@Valid @RequestBody Film film) {
        String out;
        if (!filmStorage.containsKey(film.getId())) {
            out = "This id is not available for PUT-method. Film with id " + film.getId() + " was not be replaced.";
            log.info(out);
            return out;
        } else {
            try {
                if (film.getName().isBlank()) {
                    out = "Name of film cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getDescription().length() > 200) {
                    out = "Description length cannot be more than 200 char.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getReleaseData() <= -27032) {
                    out = "Release date must be after 28.12.1895.(27032 days before Unix-epoch)";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getDuration() <= 0) {
                    out = "Film duration must be non-negative.";
                    log.error(out);
                    throw new NotValidException(out);
                } else {
                    log.info("Used PUT-method. Film with id " + film.getId() + " was replaced.");
                    filmStorage.put(film.getId(), film);
                    return gson.toJson(film);
                }
            } catch (NotValidException e) {
                return e.getMessage();
            }
        }
    }

    @PostMapping("/film")
    public String setFilm(@RequestBody Film film) {
        String out;
        if (filmStorage.containsKey(film.getId())) {
            out = "Film with this id is already added. Film was not be added.";
            log.info(out);
            return out;
        } else {
            try {
                if (film.getName().isBlank()) {
                    out = "Name of film cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getDescription().length() > 200) {
                    out = "Description length cannot be more than 200 char.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getReleaseData() < -27032) {
                    out = "Release date must be after 28.12.1895.(27032 days before Unix-epoch)";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (film.getDuration() < 0) {
                    out = "Film duration must be non-negative.";
                    log.error(out);
                    throw new NotValidException(out);
                } else {
                    log.info("Used POST-method. Film with id " + film.getId() + " was added.");
                    filmStorage.put(film.getId(), film);
                    return gson.toJson(film);
                }
            } catch (NotValidException e) {
                return e.getMessage();
            }
        }
    }

}