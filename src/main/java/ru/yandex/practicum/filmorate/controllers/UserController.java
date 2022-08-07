package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotValidMethodException;
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
    private Integer counter = 1;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET enabled. List of users was successfully sent.");
        return new ArrayList<>(userStorage.values());
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody  User user) {
        if (!userStorage.containsKey(user.getId())) {
            log.error("Cannot find user with this id. User with id " + user.getId() + " was not be replaced.");
            throw new NotValidMethodException("User with id="+user.getId()+" not found");
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    @PostMapping("/users")
    public User setUser(@RequestBody @Valid User user) {
        if (userStorage.containsKey(user.getId())) {
            log.error("User with this id is already added. User was not be added.");
            throw new NotValidMethodException("User with id="+user.getId()+" is already added.");
        } else {
            if (user.getId() == null){
                user.setId(generateId());
            } else {
                if (user.getId()>counter){
                    counter=user.getId();
                }
            }
            if (user.getName().equals("")){
                user.setName(user.getLogin());
            }
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    private Integer generateId(){
        return counter++;
    }

}