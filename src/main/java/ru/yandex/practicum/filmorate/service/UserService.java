package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyCreatedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.FriendshipError;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private Integer counter = 1;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public String addFriend(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        if (!inMemoryUserStorage.getUsersById(userId).getFriendsId().isEmpty()) {
            if (inMemoryUserStorage.getUsersById(userId).getFriendsId().contains(friendId)) {
                log.error("Пользователи уже дружат!");
                throw new FriendshipError("Пользователи уже дружат!");
            }
        }
        inMemoryUserStorage.getUsersById(userId).getFriendsId().add(friendId);
        inMemoryUserStorage.getUsersById(friendId).getFriendsId().add(userId);
        String message = "Пользователь " + inMemoryUserStorage.getUsersById(userId).getName() +
                "(userId=" + userId + ") подружился(-лась) с пользователем " +
                inMemoryUserStorage.getUsersById(friendId).getName() + "(userId=" + friendId + ").";
        log.info(message);
        return message;
    }

    public String removeFriend(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        if (inMemoryUserStorage.getUsersById(userId).getFriendsId().isEmpty() ||
                !inMemoryUserStorage.getUsersById(userId).getFriendsId().contains(friendId)) {
            log.error("Пользователи не дружат!");
            throw new FriendshipError("Пользователи не дружат!");
        }
        inMemoryUserStorage.getUsersById(userId).getFriendsId().remove(friendId);
        inMemoryUserStorage.getUsersById(friendId).getFriendsId().remove(userId);
        String message = "Пользователь " + inMemoryUserStorage.getUsersById(userId).getName() +
                "(userId=" + userId + ") успешно разорвал дружбу с пользователем " +
                inMemoryUserStorage.getUsersById(friendId).getName() + "(userId=" + friendId + ").";
        log.info(message);
        return message;
    }

    public Set<User> commonFriends(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        log.info("GET enabled. List of common friends(id_1=" + userId +
                ", id_2=" + friendId + ") was successfully sent.");
        Set<Integer> commonFriends = new HashSet<>();
        if (!inMemoryUserStorage.getUsersById(userId).getFriendsId().isEmpty()) {
            for (Integer id : inMemoryUserStorage.getUsersById(userId).getFriendsId()) {
                if (inMemoryUserStorage.getUsersById(friendId).getFriendsId().contains(id)) {
                    commonFriends.add(id);
                }
            }
            return commonFriends.stream().map(e -> inMemoryUserStorage.getUsersById(e)).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }

    }

    public Set<User> listOfFriends(Integer userId) {
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("User with id=" + userId + " not found");
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        log.info("GET enabled. List of friends(user_id=" + userId + ") was successfully sent.");
        if (!inMemoryUserStorage.getUsersById(userId).getFriendsId().isEmpty()) {
            return inMemoryUserStorage.getUsersById(userId).getFriendsId().stream()
                    .map(e -> inMemoryUserStorage.getUsersById(e)).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public List<User> getAllUsers() {
        log.info("GET enabled. List of users was successfully sent.");
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUsersById(Integer id) {
        if (inMemoryUserStorage.isNotContains(id)) {
            log.error("User with id=" + id + " not found");
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }
        log.info("User with id=" + id + " was successfully sent.");
        return inMemoryUserStorage.getUsersById(id);
    }

    public User putUser(User user) {
        if (inMemoryUserStorage.isNotContains(user.getId())) {
            log.error("Cannot find user with this id. User with id " + user.getId() + " was not be replaced.");
            throw new NotFoundException("User with id=" + user.getId() + " not found");
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            return inMemoryUserStorage.putUser(user);
        }
    }

    public User setUser(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        } else {
            if (user.getId() > counter) {
                counter = user.getId();
            }
        }
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (!inMemoryUserStorage.isNotContains(user.getId())) {
            log.error("User with this id is already added. User was not be added.");
            throw new AlreadyCreatedException("User with id=" + user.getId() + " is already added.");
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            return inMemoryUserStorage.setUser(user);
        }
    }

    private void exceptionChecker(Integer userId, Integer friendId) {
        if (inMemoryUserStorage.isNotContains(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (inMemoryUserStorage.isNotContains(friendId)) {
            log.error("Пользователь с id " + friendId + " не найден.");
            throw new NotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        if (userId.equals(friendId)) {
            log.error("Дружба возможна только между разными пользователями.");
            throw new FriendshipError("Дружба возможна только между разными пользователями.");
        }
    }

    private Integer generateId() {
        return counter++;
    }

}
