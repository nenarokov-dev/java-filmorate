package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
@Service
@Slf4j
public class GenreService {

    private final GenreDaoImpl genreDao;

    @Autowired
    public GenreService(GenreDaoImpl genreDao){
        this.genreDao=genreDao;
    }


    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre getGenreById(Integer genreId) {
        try {
            return genreDao.getGenreById(genreId);
        } catch (EmptyResultDataAccessException e){
            String message = "Не удалось получить жанр с genre_id="+genreId;
            log.error(message);
            throw new NotFoundException(message);
        }
    }

}
