package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.interfaces.GenreContract;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class GenreService implements GenreContract {

    private final GenreDbStorage genreDao;

    public List<Genre> getAll() {
        return genreDao.getAll();
    }

    public Genre getById(Integer genreId) {
        try {
            return genreDao.getById(genreId);
        } catch (EmptyResultDataAccessException e) {
            String message = "Не удалось получить жанр с genre_id=" + genreId;
            log.error(message);
            throw new NotFoundException(message);
        }
    }

}
