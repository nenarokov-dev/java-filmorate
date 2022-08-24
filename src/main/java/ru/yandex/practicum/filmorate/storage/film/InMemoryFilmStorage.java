package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> filmStorage = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        if (filmStorage.isEmpty()){
            return Collections.emptyList();
        } else {
            return new ArrayList<>(filmStorage.values());
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        return filmStorage.get(id);
    }

    @Override
    public Film putFilm(Film film) {
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film setFilm(Film film) {
        filmStorage.put(film.getId(), film);
        return film;

    }

    public boolean isThisFilmContained(Integer filmId) {
        return filmStorage.get(filmId) != null;
    }

}
