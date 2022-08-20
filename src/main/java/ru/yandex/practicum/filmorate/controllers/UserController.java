package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FriendshipError;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET enabled. List of users was successfully sent.");
        return inMemoryUserStorage.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("User with id=" + userId + " not found");
            throw new BeanNotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        log.info("User with id=" + userId + " was successfully sent.");
        return inMemoryUserStorage.getUsersById(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public Set<User> getUsersCommonFriends(@PathVariable Integer userId, @PathVariable Integer friendId) {
        exceptionChecker(userId, friendId);
        log.info("GET enabled. List of common friends(id_1=" + userId +
                ", id_2=" + friendId + ") was successfully sent.");
        if (!userService.commonFriends(userId, friendId).isEmpty()) {
            return userService.commonFriends(userId, friendId).stream()
                    .map(inMemoryUserStorage::getUsersById).collect(Collectors.toSet());
        } else return Collections.emptySet();
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUsersFriends(@PathVariable Integer userId) {
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("User with id=" + userId + " not found");
            throw new BeanNotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        log.info("GET enabled. List of friends(user_id=" + userId + ") was successfully sent.");
        if (userService.listOfFriends(userId) != null) {
            return userService.listOfFriends(userId).stream()
                    .map(inMemoryUserStorage::getUsersById).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.putUser(user);
    }

    @PostMapping
    public User setUser(@RequestBody @Valid User user) {
        return inMemoryUserStorage.setUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public String addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        exceptionChecker(userId, friendId);
        if (userService.listOfFriends(userId) != null) {
            if (userService.listOfFriends(userId).contains(friendId)) {
                log.error("Пользователи уже дружат!");
                throw new FriendshipError("Пользователи уже дружат!");
            }
        }
        userService.addFriend(userId, friendId);
        String message = "Пользователь " + inMemoryUserStorage.getUsersById(userId).getName() +
                "(userId=" + userId + ") подружился(-лась) с пользователем " +
                inMemoryUserStorage.getUsersById(friendId).getName() + "(userId=" + friendId + ").";
        log.info(message);
        return message;
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public String deleteFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        exceptionChecker(userId, friendId);
        if (userService.listOfFriends(userId) == null || !userService.listOfFriends(userId).contains(friendId)) {
            log.error("Пользователи не дружат!");
            throw new FriendshipError("Пользователи не дружат!");
        }
        userService.removeFriend(userId, friendId);
        String message = "Пользователь " + inMemoryUserStorage.getUsersById(userId).getName() +
                "(userId=" + userId + ") успешно разорвал дружбу с пользователем " +
                inMemoryUserStorage.getUsersById(friendId).getName() + "(userId=" + friendId + ").";
        log.info(message);
        return message;
    }

    private void exceptionChecker(Integer userId, Integer friendId) {
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new BeanNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (inMemoryUserStorage.isNotContains(friendId)) {
            log.error("Пользователь с id " + friendId + " не найден.");
            throw new BeanNotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        if (userId.equals(friendId)) {
            log.error("Дружба возможна только между разными пользователями.");
            throw new FriendshipError("Дружба возможна только между разными пользователями.");
        }
    }


}