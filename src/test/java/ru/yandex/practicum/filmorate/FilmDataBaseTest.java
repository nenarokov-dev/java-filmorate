package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDataBaseTest {

    private final FilmDbStorage filmDao;
    private final UserDbStorage userDao;
    private final LikesDbStorage likesDao;

    Film film = new Film(1, "sfsd", "sdfsdf", LocalDate.of(2000, 1, 1),
            120, new FilmRating(1, null));

    Film filmUpdate = new Film(1, "Аквамен", "Рыба", LocalDate.of(2000, 1, 1),
            120, new FilmRating(1, null));

    User user = new User(1, "sdfsd", "fghdgfh", "sdfdf@yandex.ru",
            LocalDate.of(2000, 1, 1));

    @Test
    void saveFilmIntoDataBaseTest(){
        filmDao.save(film);
        Film film1 = filmDao.getFilmById(1);
        assertEquals(film1.getId(), 1);
    }

    @Test
    void updateFilmInDataBaseTest(){
        filmDao.save(film);
        filmDao.update(filmUpdate);
        Film film1 = filmDao.getFilmById(1);
        assertEquals(film1.getName(), filmUpdate.getName());
    }

    @Test
    void getFilmByIdFromDataBaseTest() {
        filmDao.save(film);
        Film film1 = filmDao.getFilmById(film.getId());
        assertEquals(film1.getName(), film.getName());
        assertEquals(film1.getDescription(), film.getDescription());
        assertEquals(film1.getDuration(), film.getDuration());
        assertEquals(film1.getReleaseDate(), film.getReleaseDate());
        assertEquals(film1.getMpa().getId(), film.getMpa().getId());
    }

    @Test
    void getAllFilmsFromDataBaseTest() {
        filmDao.save(film);
        Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa());
        film1.setId(2);
        filmDao.save(film1);
        List<Film> films = filmDao.getAllFilms();
        assertEquals(films.get(0).getId(), film.getId());
        assertEquals(films.get(1).getId(), film1.getId());
    }

    @Test
    void addLikeToFilmTest() {
        userDao.save(user);
        filmDao.save(film);
        likesDao.addLike(user.getId(), film.getId());
        assertTrue(likesDao.getUserWhoLikesFilm(film.getId()).contains(user.getId()));
    }

    @Test
    void removeLikeFromFilmTest() {
        userDao.save(user);
        filmDao.save(film);
        likesDao.addLike(user.getId(), film.getId());
        likesDao.removeLike(user.getId(), film.getId());
        Film film1 = filmDao.getFilmById(film.getId());
        assertFalse(film1.getUsersIdWhoLikes().contains(user.getId()));
    }

    @Test
    void getPopularFilmsFromDataBaseTest() {
        filmDao.save(film);
        Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa());
        film1.setId(2);
        filmDao.save(film1);
        userDao.save(user);
        likesDao.addLike(user.getId(), 2);
        List<Film> prioritizedFilms = filmDao.getPopularFilms(1);
        assertEquals(prioritizedFilms.get(0).getId(), 2);
    }

}

