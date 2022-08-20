package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BeanAlreadyCreatedException;
import ru.yandex.practicum.filmorate.exceptions.BeanNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userStorage = new HashMap<>();
    private Integer counter = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUsersById(Integer id) {
        return userStorage.get(id);
    }

    @Override
    public User putUser(User user) {
        if (!userStorage.containsKey(user.getId())) {
            log.error("Cannot find user with this id. User with id " + user.getId() + " was not be replaced.");
            throw new BeanNotFoundException("User with id=" + user.getId() + " not found");
        } else {
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public User setUser(User user) {
        if (userStorage.containsKey(user.getId())) {
            log.error("User with this id is already added. User was not be added.");
            throw new BeanAlreadyCreatedException("User with id=" + user.getId() + " is already added.");
        } else {
            if (user.getId() == null) {
                user.setId(generateId());
            } else {
                if (user.getId() > counter) {
                    counter = user.getId();
                }
            }
            if (user.getName().equals("")) {//поставил @NotNull в поле name у User, а если логин=null - то до сюда и не
                user.setName(user.getLogin());                                                //  дойдёт(NotValid)
            }
            log.info("Used POST-method. User with id " + user.getId() + " was added.");
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    public boolean isNotContains(Integer id) {
        return !userStorage.containsKey(id);
    }

    private Integer generateId() {
        return counter++;
    }

}
