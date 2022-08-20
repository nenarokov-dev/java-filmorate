package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Validated
class UserControllerTests {

    @Test
    void getUsersTest() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService();
        UserController uc = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "JavaGod", "Hacker", "java@yandex.ru",
                LocalDate.of(1990, 12, 12));
        uc.setUser(user);
        assertEquals(uc.getAllUsers().get(0), user);
    }

    @Test
    void setUsersTest() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService();
        UserController uc = new UserController(inMemoryUserStorage, userService);
        User notCorrectUser = new User(1, "JavaGod", "Hacker", "java@yandex.ru",
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
        assertEquals("User with id=" + notCorrectUser.getId() + " is already added.", exception.getMessage());
    }

    @Test
    void putUsersTest() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService();
        UserController uc = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "JavaGod", "Hacker", "java@yandex.ru",
                LocalDate.of(1990, 12, 12));
        final BeanNotFoundException exception = assertThrows(
                BeanNotFoundException.class,
                () -> uc.putUser(user));
        assertEquals("User with id=" + user.getId() + " not found", exception.getMessage());
        uc.setUser(user);
        User user2 = new User(1, "PythonGod", "Junior", "python@yandex.ru",
                LocalDate.of(2000, 1, 1));
        uc.putUser(user2);
        assertEquals(uc.getAllUsers().get(0), user2);
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

