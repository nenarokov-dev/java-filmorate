package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exception.FriendshipError;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.interfaces.UsersContract;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService implements UsersContract {

    private Integer maxUserId;
    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
        this.maxUserId = userDao.getMaxUserId();
    }

    @Override
    public User setUser(User user) throws SQLException {
        if ((user.getId() == null) || (user.getId() <= 0)) {
            user.setId(generateId());
        } else {
            if (user.getId() > maxUserId) {
                maxUserId = user.getId();
            }
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        try {
            userDao.setUser(user);
            log.info("Пользователь user_id=" + user.getId() + " успешно добавлен.");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при добавлении пользователя user_id=" + user.getId() + " в базу данных.";
            log.error(message);
            throw new SQLException(message);
        }
    }

    @Override
    public User putUser(User user) throws SQLException {
        try {
            userDao.putUser(user);
            log.info("Пользователь user_id=" + user.getId() + " успешно обновлён.");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при обновлении пользователя user_id=" + user.getId() + ". Пользователь не найден.";
            log.error(message);
            throw new NotFoundException(message);
        } catch (SQLException e) {
            String message = "Ошибка при обновлении пользователя user_id" + user.getId() + " в базе данных.";
            log.error(message);
            throw new SQLException(message);
        }
    }

    @Override
    public User getUsersById(Integer id) {
        try {
            User user = userDao.getUsersById(id);
            log.info("Пользователь user_id=" + id + " успешно получен.");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при извлечении пользователя user_id=" + id + " из базы данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = userDao.getAllUsers();
            log.info("Список пользователей успешно получен.");
            return users;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при получении списка всех пользователей из базы данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public String addFriend(Integer userId, Integer friendId) throws SQLException {
        exceptionChecker(userId, friendId);
        try {
            String message = userDao.addFriend(userId, friendId);
            log.info(message);
            return message;
        } catch (SQLException e) {
            String message = "Ошибка при добавлении пользователя user_id=" + userId +
                    " в друзья пользователю user_id=" + friendId + ".";
            log.error(message);
            throw new SQLException();
        }
    }

    @Override
    public String removeFriend(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        String message = userDao.removeFriend(userId, friendId);
        log.info(message);//ошибки не будет т.к. при удалении несуществующей дружбы ничего не произойдёт.
        return message;
    }

    @Override
    public Set<User> listOfFriends(Integer userId) {
        try {
            Set<User> listOfFriends = userDao.listOfFriends(userId);
            log.info("Список объектов друзей пользователя user_id=" + userId + " успешно получен.");
            return listOfFriends;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public Set<User> commonFriends(Integer userId, Integer otherUserId) {
        exceptionChecker(userId, otherUserId);
        try {
            Set<User> friendshipList = userDao.commonFriends(userId, otherUserId);
            log.info("Список объектов общих друзей пользователей user_id=" + userId + "и user_id=" +
                    otherUserId + " успешно получен.");
            return friendshipList;
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        try {
            FriendshipStatus friendshipStatus = userDao.getFriendshipStatus(userId, friendId);
            log.info("Статус дружбы пользователей user_id=" + userId + "и user_id=" +
                    friendId + " успешно получен. Статус дружбы: " + friendshipStatus);
            return FriendshipStatus.UNCONFIRMED;
        } catch (FriendshipError e) {
            log.error(e.getMessage());
            throw new FriendshipError(e.getMessage());
        }
    }

    private void exceptionChecker(Integer userId, Integer friendId) {

        if (userId.equals(friendId)) {
            log.error("Дружба возможна только между разными пользователями.");
            throw new FriendshipError("Дружба возможна только между разными пользователями.");
        }
    }

    private Integer generateId() {
        return ++maxUserId;
    }

}
