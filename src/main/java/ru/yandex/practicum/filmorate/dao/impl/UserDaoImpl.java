package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.mappers.IntegerMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.FriendshipError;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User setUser(User user) {
        jdbcTemplate.update("INSERT INTO users VALUES (?,?,?,?,?)",
                user.getId(), user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        return user;
    }

    public User putUser(User user) throws SQLException {
        getUsersById(user.getId());
        jdbcTemplate.update("UPDATE users SET login=?,name=?,email=?,birthday=? WHERE user_id=?",
                user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    public User getUsersById(Integer userId) {
        User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id=" + userId, new UserMapper());
        if (user != null) {//идея просит заненуллить юзера, но по идее юзер никогда не null, т.к. в случае возврата
            setFriends(user);// некорректных данных вылетит IncorrectResultSizeDataAccessException
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", new UserMapper());
        users.forEach(this::setFriends);
        return users;
    }

    public String addFriend(Integer userId, Integer friendId) throws SQLException {
        jdbcTemplate.update("INSERT INTO friendship VALUES (?,?)", userId, friendId);
        return String.format("Пользователь user_id=%d добавился в друзья к пользователю user_id=%d",
                userId, friendId);
    }

    public String removeFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id=? AND friend_or_follower_id=?", userId, friendId);
        return String.format("Пользователь user_id=%d успешно удалил из друзей пользователя user_id=%d",
                userId, friendId);
    }

    public Set<User> listOfFriends(Integer userId) {
        getUsersById(userId);//в случае если юзера нет вылетит IncorrectResultSizeDataAccessException
        return getAllFriendsByUser(userId).stream().map(this::getUsersById).collect(Collectors.toSet());
    }//сделал бы тут и далее через стрим, но стрим не дружит с обработкой исключений, внешне получится то же самое.

    public Set<User> commonFriends(Integer userId, Integer otherUserId) {
        try {
            getUsersById(userId);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь user_id=" + userId + " не найден.");
        }
        try {
            getUsersById(otherUserId);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь user_id=" + otherUserId + " не найден.");
        }
        //в случае если юзера нет вылетит IncorrectResultSizeDataAccessException
        // сделал для того чтобы в сообщении об ошибке была конкретизация id пользователя, который отсутствует в бд.
        return getAllCommonFriendsByUsers(userId, otherUserId).
                stream().map(this::getUsersById).collect(Collectors.toSet());
    }

    public FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId) {

        Set<Integer> UsersListOfFriends = getAllFriendsByUser(userId);
        Set<Integer> FriendsListOfFriends = getAllFriendsByUser(friendId);
        if (UsersListOfFriends.contains(friendId) && !FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.UNCONFIRMED;
        } else if (!UsersListOfFriends.contains(friendId) && FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.UNCONFIRMED;
        } else if (UsersListOfFriends.contains(friendId) && FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.CONFIRMED;
        } else {
            throw new FriendshipError("Ошибка при получении статуса дружбы пользователей user_id=" + userId +
                    " и user_id=" + friendId + ". Пользователи не дружат.");
        }
    }

    private Set<Integer> getAllFriendsByUser(Integer userId) {
        return new HashSet<>(jdbcTemplate.query(
                String.format("SELECT friend_or_follower_id FROM friendship WHERE user_id=%d", userId),
                new IntegerMapper("friend_or_follower_id")));
    }

    private Set<Integer> getAllCommonFriendsByUsers(Integer userId, Integer otherUserId) {
        return new HashSet<>(jdbcTemplate.query("SELECT friend_or_follower_id FROM friendship WHERE user_id=" +
                        userId + " AND friend_or_follower_id IN " + "(SELECT  friend_or_follower_id FROM friendship " +
                        "WHERE USER_ID=" + otherUserId + ")",
                new IntegerMapper("friend_or_follower_id")));
    }

    public Integer getMaxUserId() {
        return jdbcTemplate.query("SELECT max(user_id) AS max FROM users",
                new IntegerMapper("max")).stream().findAny().orElse(1);
    }

    private void setFriends(User user) {
        user.getFriendsId().addAll(getAllFriendsByUser(user.getId()));
    }
}

