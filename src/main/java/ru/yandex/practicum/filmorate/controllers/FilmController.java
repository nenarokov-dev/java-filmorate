package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Validated
@RestController
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> filmStorage = new HashMap<>();
    private Integer counter = 1;

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET enabled. List of films was successfully sent.");
        return new ArrayList<>(filmStorage.values());
    }

    @PutMapping("/films")
    public Film putFilm(@RequestBody @Valid Film film){
        if (!filmStorage.containsKey(film.getId())) {
            log.error("This id is not available for PUT-method. Film with id " + film.getId() + " was not be replaced.");
            throw new BeanNotFoundException("Film with id="+film.getId()+" not found");
        } else {
            log.info("Used PUT-method. Film with id " + film.getId() + " was replaced.");
            filmStorage.put(film.getId(), film);
            return film;
        }
    }

    @PostMapping("/films")
    public Film setFilm(@RequestBody @Valid Film film){
        if (filmStorage.containsKey(film.getId())) {
            log.error("Film with this id is already added. Film was not be added.");
            throw new BeanAlreadyCreatedException("Film with id="+film.getId()+" is already created.");
        } else {
            if (film.getId() == null){
                film.setId(generateId());
            } else {
                if (film.getId()>counter){
                    counter=film.getId();
                }
            }
            log.info("Used POST-method. Film with id " + film.getId() + " was added.");
            filmStorage.put(film.getId(), film);
            return film;
        }
    }

    private Integer generateId(){
        return counter++;
    }


}