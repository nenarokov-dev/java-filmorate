package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyCreatedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private Integer counter = 1;

    private final InMemoryUserStorage inMemoryUserStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private Map<Integer, Long> prioritizedFilms = new HashMap<>();

    @Autowired//без этого конструктора при обращении к inMemoryFilmStorage получаю NullPointerException
    public FilmService(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<Film> getAllFilms() {
        log.info("Used GET-method. List of films was successfully sent.");
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        filmIdCheck(id);
        log.info("Used GET-method. Film with id=" + id + " was successfully sent.");
        return inMemoryFilmStorage.getFilmById(id);
    }

    public Film putFilm(Film film) {
        filmIdCheck(film.getId());
        log.info("Used PUT-method. Film with id " + film.getId() + " was replaced.");
        return inMemoryFilmStorage.putFilm(film);
    }

    public Film setFilm(Film film) {
        if (film.getId() == null) {
            film.setId(generateId());
        } else {
            if (film.getId() > counter) {
                counter = film.getId();
            }
        }
        if (inMemoryFilmStorage.isThisFilmContained(film.getId())) {
            log.error("Film with this id is already added. Film was not be added.");
            throw new AlreadyCreatedException("Film with id=" + film.getId() + " is already created.");
        }
        log.info("Used POST-method. Film with id " + film.getId() + " was added.");
        Film createdFilm = inMemoryFilmStorage.setFilm(film);
        prioritizedFilmsUpdater();
        return createdFilm;
    }

    public Film addLike(Integer userId, Integer filmId) {
        filmAndUserIdCheck(userId, filmId);
        log.info("Used PUT-method.User with id=" + userId + " add like to film with id=" + filmId + ".");
        inMemoryFilmStorage.getFilmById(filmId).getUsersIdWhoLikes().add(userId);
        prioritizedFilmsUpdater();
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    public Film removeLike(Integer userId, Integer filmId) {
        filmAndUserIdCheck(userId, filmId);
        log.info("Used DELETE-method.User with id=" + userId + " remove like from film with id=" + filmId + ".");
        inMemoryFilmStorage.getFilmById(filmId).getUsersIdWhoLikes().add(userId);
        prioritizedFilmsUpdater();
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    public List<Film> getPopularFilms(Integer counter) {
        log.info("Used GET-method.List of prioritized films was successfully sent.");
        if (!prioritizedFilms.keySet().isEmpty()) {
            if (prioritizedFilms.size() < counter) {
                return prioritizedFilms.keySet().stream().map(e -> inMemoryFilmStorage.getFilmById(e)).
                        collect(Collectors.toList());
            } else {
                return prioritizedFilms.keySet().stream().map(e -> inMemoryFilmStorage.getFilmById(e)).
                        limit(counter).collect(Collectors.toList());
            }
        } else return Collections.emptyList();
    }

    /* Думаю вскоре этот метод пригодится, а пока пусть побудет в сером цвете)
    public Long getLikesCounter(Integer id) {
        return prioritizedFilms.get(id);
    }*/

    private void prioritizedFilmsUpdater() {
        if (!prioritizedFilms.isEmpty()) {
            prioritizedFilms.clear();
        }
        for (Film film : inMemoryFilmStorage.getAllFilms()) {
            long numOfLikes = film.getUsersIdWhoLikes().size();
            prioritizedFilms.put(film.getId(), numOfLikes);
        }
        prioritizedFilms = sorter(prioritizedFilms);
    }

    private Map<Integer, Long> sorter(Map<Integer, Long> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        }, LinkedHashMap::new
                ));
    }

    private void filmAndUserIdCheck(Integer userId, Integer filmId) {
        filmIdCheck(filmId);
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("Пользователь с id=" + userId + " не найден.");
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    private void filmIdCheck(Integer filmId) {
        if (!inMemoryFilmStorage.isThisFilmContained(filmId)) {
            log.error("Фильм с id=" + filmId + " не найден.");
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
    }

    private Integer generateId() {
        return counter++;
    }
}