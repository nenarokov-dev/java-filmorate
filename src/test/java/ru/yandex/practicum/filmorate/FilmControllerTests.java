package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Validated
class FilmControllerTests {

    @Test
    void getFilmsTest() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        FilmController fc = new FilmController(inMemoryFilmStorage, filmService, inMemoryUserStorage);
        Film aquaMan = new Film(1, "Аквамен", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        fc.setFilm(aquaMan);
        assertEquals(fc.getAllFilms().get(0), aquaMan);
    }

    @Test
    void setFilmsTest() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        FilmController fc = new FilmController(inMemoryFilmStorage, filmService, inMemoryUserStorage);
        //check bad request
        Film notCorrectFilmAquaMan = new Film(1, "Аквамен",
                "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), -133L);
        final ConstraintViolationException filmDurationException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectFilmAquaMan));
        assertEquals(filmDurationException.getMessage().substring(10),
                "Длительность фильма должна быть положительной.");
        notCorrectFilmAquaMan.setDuration(133L);//возвращаем положительное значение длительности
        notCorrectFilmAquaMan.setDescription("q".repeat(250));
        final ConstraintViolationException filmDescriptionException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectFilmAquaMan));
        assertEquals(filmDescriptionException.getMessage().substring(13),
                "Описание не должно превышать 200 символов.");
        notCorrectFilmAquaMan.setDescription("Фильм про коронование подводного из Пацанов");
        notCorrectFilmAquaMan.setName("");
        final ConstraintViolationException emptyNameException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectFilmAquaMan));
        assertEquals(emptyNameException.getMessage().substring(6), "Имя не должно быть пустым");
        notCorrectFilmAquaMan.setName("Аквамен");
        notCorrectFilmAquaMan.setReleaseDate(LocalDate.of(1800, 10, 10));
        final ConstraintViolationException notValidDateException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectFilmAquaMan));
        assertEquals(notValidDateException.getMessage().substring(13),
                "Дата релиза не должна быть ранее 28 декабря 1895 года.");
        notCorrectFilmAquaMan.setReleaseDate(LocalDate.of(2018, 12, 13));
        fc.setFilm(notCorrectFilmAquaMan);
        final BeanAlreadyCreatedException methodException = assertThrows(
                BeanAlreadyCreatedException.class, () -> fc.setFilm(notCorrectFilmAquaMan));
        assertEquals(methodException.getMessage(),
                "Film with id=" + notCorrectFilmAquaMan.getId() + " is already created.");
    }

    @Test
    void putFilmsTest() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        FilmController fc = new FilmController(inMemoryFilmStorage, filmService, inMemoryUserStorage);
        Film aquaMan = new Film(1, "Аквамен", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        final BeanNotFoundException exception = assertThrows(
                BeanNotFoundException.class,
                () -> fc.putFilm(aquaMan));
        assertEquals(exception.getMessage(), "Фильм с id=" + aquaMan.getId() + " не найден.");
        fc.setFilm(aquaMan);
        Film aquaMan2 = new Film(1, "Аквамен2", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        fc.putFilm(aquaMan2);
        assertEquals(fc.getAllFilms().get(0), aquaMan2);
    }

    void validateInput(Film film) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

