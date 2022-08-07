package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Validated
@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> userStorage = new HashMap<>();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET enabled. List of users was successfully sent.");
        return new ArrayList<>(userStorage.values());
    }

    @PutMapping("/users")
    public User putUser(@RequestBody @Valid User user) {
        if (!userStorage.containsKey(user.getId())) {
            log.info("Cannot find user with this id. User with id " + user.getId() + " was not be replaced.");
            return null;
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    @PostMapping("/users")
    public User setUser(@RequestBody @Valid User user) {
        if (userStorage.containsKey(user.getId())) {
            log.info("User with this id is already added. User was not be added.");
            return null;
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

}