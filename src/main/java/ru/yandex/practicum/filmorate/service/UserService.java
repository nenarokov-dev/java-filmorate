package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyCreatedException;
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

    private final UserDbStorage userDao;

    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(UserDbStorage userDao, FriendshipDao friendshipDao) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
    }

    @Override
    public User save(User user) throws SQLException {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        try {
            userDao.save(user);
            log.info("Пользователь user_id=" + user.getId() + " успешно добавлен.");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при добавлении пользователя user_id=" + user.getId() + " в базу данных.";
            log.error(message);
            throw new SQLException(message);
        }
    }

    @Override
    public User update(User user) {
        try {
            userDao.update(user);
            log.info("Пользователь user_id=" + user.getId() + " успешно обновлён.");
            return getUsersById(user.getId());
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при обновлении пользователя user_id=" + user.getId() + ". Пользователь не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public User getUsersById(Integer id) {
        try {
            User user = userDao.getUsersById(id);
            user.getFriendsId().addAll(friendshipDao.getFriendsByUser(id));
            log.info("Пользователь user_id=" + id + " успешно получен.");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при извлечении пользователя user_id=" + id + " из базы данных." +
                    " Пользователь не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = userDao.getAllUsers();
            users.forEach(e -> e.getFriendsId().addAll(friendshipDao.getFriendsByUser(e.getId())));
            log.info("Список пользователей успешно получен.");
            return users;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при получении списка всех пользователей из базы данных.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public String addFriend(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        try {
            String message = friendshipDao.addFriend(userId, friendId);
            log.info(message);
            return message;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Ошибка при добавлении пользователя user_id=" + userId +
                    " в друзья пользователю user_id=" + friendId + ". Пользователь уже добавлен в друзья.";
            log.error(message);
            throw new AlreadyCreatedException(message);
        }
    }

    @Override
    public String removeFriend(Integer userId, Integer friendId) {
        exceptionChecker(userId, friendId);
        String message = friendshipDao.removeFriend(userId, friendId);
        log.info(message);//ошибки не будет т.к. при удалении несуществующей дружбы ничего не произойдёт.
        return message;
    }

    @Override
    public Set<User> listOfFriends(Integer userId) {
        try {
            Set<User> listOfFriends = userDao.listOfFriends(userId);
            listOfFriends.forEach(e -> e.getFriendsId().addAll(friendshipDao.getFriendsByUser(e.getId())));
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
            friendshipList.forEach(e -> e.getFriendsId().addAll(friendshipDao.getFriendsByUser(e.getId())));
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
            FriendshipStatus friendshipStatus = friendshipDao.getFriendshipStatus(userId, friendId);
            log.info("Статус дружбы пользователей user_id=" + userId + "и user_id=" +
                    friendId + " успешно получен. Статус дружбы: " + friendshipStatus);
            return FriendshipStatus.UNCONFIRMED;
        } catch (FriendshipError e) {
            log.error(e.getMessage());
            throw new FriendshipError(e.getMessage());
        }
    }

    private void exceptionChecker(Integer userId, Integer friendId) {
        getUsersById(userId);
        getUsersById(friendId);
        if (userId.equals(friendId)) {
            log.error("Дружба возможна только между разными пользователями.");
            throw new FriendshipError("Дружба возможна только между разными пользователями.");
        }
    }

}
