package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateControllersTests {
    private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting();
    private static final Gson gson = gsonBuilder.create();

    @Test
    void getFilmsTest() {
        FilmController fc = new FilmController();
        Film aquaman = new Film("Аквамен", LocalDate.of(2018,12,13),
                "Фильм про коронование подводного из Пацанов", Duration.ofMinutes(133));
        aquaman.setId(1);
        fc.setFilm(aquaman);
        //ArrayList<Film> films = gson.fromJson(fc.getAllFilms(),new TypeToken<ArrayList<Film>>(){}.getType());
        assertEquals(fc.getAllFilms().get(0),aquaman);
    }

    @Test
    void setFilmsTest() {
        FilmController fc = new FilmController();
        //check bad request
        Film notCorrectFilmAquaman = new Film("Аквамен", LocalDate.of(2018, 12, 13),
                "Фильм про коронование подводного из Пацанов", Duration.ofMinutes(-133));
        notCorrectFilmAquaman.setId(1);
        assertEquals(fc.setFilm(notCorrectFilmAquaman),"Film duration must be non-negative.");
        notCorrectFilmAquaman.setDuration(133L);//возвращаем положительное значение длительности
        notCorrectFilmAquaman.setDescription("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        assertEquals(fc.setFilm(notCorrectFilmAquaman),"Description length cannot be more than 200 char.");
        notCorrectFilmAquaman.setDescription("Фильм про коронование подводного из Пацанов");
        notCorrectFilmAquaman.setName("");
        assertEquals(fc.setFilm(notCorrectFilmAquaman),"Name of film cannot be blank.");
        notCorrectFilmAquaman.setName("Аквамен");
        notCorrectFilmAquaman.setReleaseData(-50000L);
        assertEquals(fc.setFilm(notCorrectFilmAquaman),
                "Release date must be after 28.12.1895.(27032 days before Unix-epoch)");
        notCorrectFilmAquaman.setReleaseData(LocalDate.of(2018, 12, 13).toEpochDay());
        fc.setFilm(notCorrectFilmAquaman);
        assertEquals(fc.setFilm(notCorrectFilmAquaman),"Film with this id is already added. Film was not be added.");
    }

    @Test
    void putFilmsTest() {
        FilmController fc = new FilmController();
        //check bad request
        Film aquaman = new Film("Аквамен", LocalDate.of(2018, 12, 13),
                "Фильм про коронование подводного из Пацанов", Duration.ofMinutes(133));
        aquaman.setId(1);
        assertEquals(fc.putFilm(aquaman),
                "This id is not available for PUT-method. Film with id " + aquaman.getId() +
                        " was not be replaced.");
        fc.setFilm(aquaman);
        Film aquaman2 = new Film("Аквамен2", LocalDate.of(2018, 12, 13),
                "Фильм про коронование подводного из Пацанов", Duration.ofMinutes(133));
        aquaman2.setId(1);
        fc.putFilm(aquaman2);
        //ArrayList<Film> films = gson.fromJson(fc.getAllFilms(),new TypeToken<ArrayList<Film>>(){}.getType());
        assertEquals(fc.getAllFilms().get(0),aquaman2);
        //проверять валидность полей класса film нет смысла т.к. они были проверены в методе setFilmsTest().
    }

    @Test
    void getUsersTest() {
        UserController uc = new UserController();
        User user = new User("java@yandex.ru", "JavaGod",
                "Hacker", LocalDate.of(1990,12,12).toEpochDay());
        user.setId(1);
        uc.setUser(user);
        ArrayList<User> users = gson.fromJson(uc.getAllUsers(),new TypeToken<ArrayList<User>>(){}.getType());
        assertEquals(users.get(0),user);
    }

    @Test
    void setUsersTest() {
        UserController uc = new UserController();
        User notCorrectUser = new User("java@yandex.ru", "JavaGod",
                "Hacker", LocalDate.of(1990,12,12).toEpochDay());
        notCorrectUser.setId(1);
        notCorrectUser.setLogin("");
        assertEquals(uc.setUser(notCorrectUser),"User's login cannot be blank.");
        notCorrectUser.setLogin("Java God");
        assertEquals(uc.setUser(notCorrectUser),"User's login cannot contains spaces.");
        notCorrectUser.setLogin("JavaGod");
        notCorrectUser.setEmail("");
        assertEquals(uc.setUser(notCorrectUser),"User's email cannot be blank.");
        notCorrectUser.setEmail("fgfhfgf");
        assertEquals(uc.setUser(notCorrectUser),"User's email must have email-structure with '@'");
        notCorrectUser.setEmail("java@yandex.ru");
        notCorrectUser.setBirthday(LocalDate.MAX.toEpochDay());
        assertEquals(uc.setUser(notCorrectUser),"User cannot be born in the future.");
        notCorrectUser.setBirthday(LocalDate.of(1990,12,12).toEpochDay());
        uc.setUser(notCorrectUser);
        assertEquals(uc.setUser(notCorrectUser),"User with this id is already added. User was not be added.");
    }

    @Test
    void putUsersTest() {
        UserController uc = new UserController();
        User user = new User("java@yandex.ru", "JavaGod",
                "Hacker", LocalDate.of(1990,12,12).toEpochDay());
        user.setId(1);
        assertEquals(uc.putUser(user),"Cannot find user with this id. User with id " + user.getId() +
                " was not be replaced.");
        uc.setUser(user);
        User user2 = new User("python@yandex.ru", "PythonGod",
                "Junior", LocalDate.of(2000,1,1).toEpochDay());
        user2.setId(1);
        uc.putUser(user2);
        ArrayList<User> users = gson.fromJson(uc.getAllUsers(),new TypeToken<ArrayList<User>>(){}.getType());
        assertEquals(users.get(0),user2);
    }

}

