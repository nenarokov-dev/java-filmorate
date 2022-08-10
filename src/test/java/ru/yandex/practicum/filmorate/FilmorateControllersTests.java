package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Validated
class FilmorateControllersTests {

    @Test
    void getFilmsTest() {
        FilmController fc = new FilmController();
        Film aquaMan = new Film(1, "Аквамен", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        fc.setFilm(aquaMan);
        assertEquals(fc.getAllFilms().get(0), aquaMan);
    }

    @Test
    void setFilmsTest() {
        FilmController fc = new FilmController();
        //check bad request
        Film notCorrectFilmAquaMan = new Film(1, "Аквамен",
                "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13),-133L);
        final ConstraintViolationException filmDurationException = assertThrows(
                ConstraintViolationException.class, () ->validateInput(notCorrectFilmAquaMan));
        assertEquals(filmDurationException.getMessage().substring(10),
                "Длительность фильма должна быть положительной.");
        notCorrectFilmAquaMan.setDuration(133L);//возвращаем положительное значение длительности
        notCorrectFilmAquaMan.setDescription("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
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
                "Film with id="+notCorrectFilmAquaMan.getId()+" is already created.");
    }

    @Test
    void putFilmsTest() {
        FilmController fc = new FilmController();
        Film aquaMan = new Film(1, "Аквамен", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        final BeanNotFoundException exception = assertThrows(
                BeanNotFoundException.class,
                () -> fc.putFilm(aquaMan));
        assertEquals(exception.getMessage(), "Film with id="+aquaMan.getId()+" not found");
        fc.setFilm(aquaMan);
        Film aquaMan2 = new Film(1, "Аквамен2", "Фильм про коронование подводного из Пацанов",
                LocalDate.of(2018, 12, 13), 133L);
        fc.putFilm(aquaMan2);
        assertEquals(fc.getAllFilms().get(0), aquaMan2);
    }

    @Test
    void getUsersTest() {
        UserController uc = new UserController();
        User user = new User(1, "JavaGod", "Hacker", "java@yandex.ru",
                LocalDate.of(1990, 12, 12));
        uc.setUser(user);
        assertEquals(uc.getAllUsers().get(0), user);
    }

    @Test
    void setUsersTest() {
        UserController uc = new UserController();
        User notCorrectUser = new User(1, "JavaGod", "Hacker","java@yandex.ru",
                LocalDate.of(1990, 12, 12));
        notCorrectUser.setLogin("");
        final ConstraintViolationException loginEmptyException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectUser)); //exception message: "login: Логин не должен быть пустым"
        assertEquals(loginEmptyException.getMessage().substring(7),
                "Логин не должен быть пустым");
        notCorrectUser.setLogin("Java God");
        final ConstraintViolationException loginWithSpasesException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectUser));
        assertEquals(loginWithSpasesException.getMessage().substring(7),
                "Логин не должен содержать пробелов");
        notCorrectUser.setLogin("JavaGod");
        notCorrectUser.setEmail("");
        final ConstraintViolationException emailEmptyException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectUser));
        assertEquals(emailEmptyException.getMessage().substring(7), "Email не должен быть пустым");
        notCorrectUser.setEmail("fgfhfgf");
        final ConstraintViolationException notEmailException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectUser));
        assertEquals(notEmailException.getMessage().substring(7),
                "Введённая строка не обладает структурой email [***@**.**]");
        notCorrectUser.setEmail("java@yandex.ru");
        notCorrectUser.setBirthday(LocalDate.MAX);
        final ConstraintViolationException birthdayException = assertThrows(
                ConstraintViolationException.class, () -> validateInput(notCorrectUser));
        assertEquals(birthdayException.getMessage().substring(10),
                "День рождения пользователя должен быть в прошлом.");
        notCorrectUser.setBirthday(LocalDate.of(1990, 12, 12));
        uc.setUser(notCorrectUser);
        final BeanAlreadyCreatedException exception = assertThrows(
                BeanAlreadyCreatedException.class, () -> uc.setUser(notCorrectUser));
        assertEquals("User with id="+notCorrectUser.getId()+" is already added.", exception.getMessage());
    }

    @Test
    void putUsersTest() {
        UserController uc = new UserController();
        User user = new User(1, "JavaGod", "Hacker","java@yandex.ru",
                LocalDate.of(1990, 12, 12));
        final BeanNotFoundException exception = assertThrows(
                BeanNotFoundException.class,
                () -> uc.putUser(user));
        assertEquals("User with id="+user.getId()+" not found", exception.getMessage());
        uc.setUser(user);
        User user2 = new User(1, "PythonGod", "Junior","python@yandex.ru",
                LocalDate.of(2000, 1, 1));
        uc.putUser(user2);
        assertEquals(uc.getAllUsers().get(0), user2);
    }

    void validateInput(Film film) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    void validateInput(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

