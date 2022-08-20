package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> filmStorage = new HashMap<>();
    private Integer counter = 1;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());
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
        if (film.getId() == null) {
            film.setId(generateId());
        } else {
            if (film.getId() > counter) {
                counter = film.getId();
            }
        }
        filmStorage.put(film.getId(), film);
        return film;

    }

    public boolean isThisFilmContained(Integer filmId) {
        return filmStorage.containsKey(filmId);
    }

    private Integer generateId() {
        return counter++;
    }
}
