package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDataBaseTest {

    private final FilmDaoImpl filmDao;
    private final UserDaoImpl userDao;

    Film film = new Film(1, "sfsd", "sdfsdf", LocalDate.of(2000, 1, 1),
            120, new FilmRating(1, null));

    Film filmUpdate = new Film(1, "Аквамен", "Рыба", LocalDate.of(2000, 1, 1),
            120, new FilmRating(1, null));

    User user = new User(1, "sdfsd", "fghdgfh", "sdfdf@yandex.ru",
            LocalDate.of(2000, 1, 1));

    @Test
    void setFilm() throws SQLException {
        filmDao.setFilm(film);
        Film film1 = filmDao.getFilmById(1);
        assertEquals(film1.getId(), 1);
    }

    @Test
    void putFilm() throws SQLException {
        filmDao.setFilm(film);
        filmDao.putFilm(filmUpdate);
        Film film1 = filmDao.getFilmById(1);
        assertEquals(film1.getName(), filmUpdate.getName());
    }

    @Test
    void getFilmById() throws SQLException {
        filmDao.setFilm(film);
        Film film1 = filmDao.getFilmById(film.getId());
        assertEquals(film1.getName(), film.getName());
        assertEquals(film1.getDescription(), film.getDescription());
        assertEquals(film1.getDuration(), film.getDuration());
        assertEquals(film1.getReleaseDate(), film.getReleaseDate());
        assertEquals(film1.getMpa().getId(), film.getMpa().getId());
    }

    @Test
    void getAllFilms() throws SQLException {
        filmDao.setFilm(film);
        Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa());
        film1.setId(2);
        filmDao.setFilm(film1);
        List<Film> films = filmDao.getAllFilms();
        assertEquals(films.get(0).getId(), film.getId());
        assertEquals(films.get(1).getId(), film1.getId());
    }

    @Test
    void addLike() throws SQLException {
        userDao.setUser(user);
        filmDao.setFilm(film);
        filmDao.addLike(user.getId(), film.getId());
        Film film1 = filmDao.getFilmById(film.getId());
        assertTrue(film1.getUsersIdWhoLikes().contains(user.getId()));
    }

    @Test
    void removeLike() throws SQLException {
        userDao.setUser(user);
        filmDao.setFilm(film);
        filmDao.addLike(user.getId(), film.getId());
        filmDao.removeLike(user.getId(), film.getId());
        Film film1 = filmDao.getFilmById(film.getId());
        assertFalse(film1.getUsersIdWhoLikes().contains(user.getId()));
    }

    @Test
    void getPopularFilms() throws SQLException {
        filmDao.setFilm(film);
        Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa());
        film1.setId(2);
        filmDao.setFilm(film1);
        userDao.setUser(user);
        filmDao.addLike(user.getId(), 2);
        List<Film> prioritizedFilms = filmDao.getPopularFilms(1);
        assertEquals(prioritizedFilms.get(0).getId(), 2);
    }

}

