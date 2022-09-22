package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String saveMessage = "INSERT INTO users (login,name,email,birthday) VALUES (?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(saveMessage, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("UPDATE users SET login=?,name=?,email=?,birthday=? WHERE user_id=?",
                user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
    }

    @Override
    public User getUsersById(Integer userId) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id=" + userId, new UserMapper());
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public Set<User> listOfFriends(Integer userId) {
        String getAllFiendsMessage = "SELECT f.friend_or_follower_id as user_id,u.login,u.name,u.email,u.birthday " +
                "FROM friendship as f " +
                "LEFT JOIN users as u ON u.user_id = f.friend_or_follower_id " +
                "WHERE f.user_id=" + userId;
        return new HashSet<>(jdbcTemplate.query(getAllFiendsMessage, new UserMapper()));
    }

    @Override
    public Set<User> commonFriends(Integer userId, Integer otherUserId) {
        String commonFriendsMessage = "SELECT f.friend_or_follower_id as user_id,u.login,u.name,u.email,u.birthday " +
                "FROM friendship as f " +
                "LEFT JOIN users as u ON u.user_id = f.friend_or_follower_id " +
                "WHERE f.user_id=" + userId + " AND f.friend_or_follower_id IN  " +
                "(SELECT friend_or_follower_id FROM friendship WHERE user_id=" + otherUserId + ");";
        return new HashSet<>(jdbcTemplate.query(commonFriendsMessage, new UserMapper()));
    }

}
