package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> userStorage = new HashMap<>();
    GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting();
    Gson gson = gsonBuilder.create();

    @GetMapping("/users")
    public String getAllUsers() {
        log.info("GET enabled. List of users was successfully sent.");
        return gson.toJson(userStorage.values());
    }

    @PutMapping("/user")
    public String putUser(@RequestBody User user) {
        String out;
        if (!userStorage.containsKey(user.getId())) {
            out = "Cannot find user with this id. User with id " + user.getId() + " was not be replaced.";
            log.info(out);
            return out;
        } else {
            try {
                if (user.getLogin().isBlank()) {
                    out = "User's login cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (user.getLogin().contains(" ")) {
                    out = "User's login cannot contains spaces.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (user.getEmail().isBlank()) {
                    out = "User's email cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (!user.getEmail().contains("@")) {
                    out = "User's email must have email-structure with '@'";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (LocalDate.ofEpochDay(user.getBirthday()).isAfter(LocalDate.now())) {
                    out = "User cannot be born in the future.";
                    log.error(out);
                    throw new NotValidException(out);
                } else {
                    log.info("Used POST-method. User with id " + user.getId() + " was added.");
                    userStorage.put(user.getId(), user);
                    return gson.toJson(user);
                }
            } catch (NotValidException e) {
                return e.getMessage();
            }
        }
    }

    @PostMapping("/user")
    public String setUser(@RequestBody User user) {
        String out;
        if (userStorage.containsKey(user.getId())) {
            out = "User with this id is already added. User was not be added.";
            log.info(out);
            return out;
        } else {
            try {
                if (user.getLogin().isBlank()) {
                    out = "User's login cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (user.getLogin().contains(" ")) {
                    out = "User's login cannot contains spaces.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (user.getEmail().isBlank()) {
                    out = "User's email cannot be blank.";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (!user.getEmail().contains("@")) {
                    out = "User's email must have email-structure with '@'";
                    log.error(out);
                    throw new NotValidException(out);
                } else if (LocalDate.ofEpochDay(user.getBirthday()).isAfter(LocalDate.now())) {
                    out = "User cannot be born in the future.";
                    log.error(out);
                    throw new NotValidException(out);
                } else {
                    log.info("Used POST-method. User with id " + user.getId() + " was added.");
                    userStorage.put(user.getId(), user);
                    return gson.toJson(user);
                }
            } catch (NotValidException e) {
                return e.getMessage();
            }
        }
    }

}