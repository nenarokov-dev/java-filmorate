package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FriendshipDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDataBaseTest {

    private final UserDbStorage userDao;

    private final FriendshipDaoImpl friendshipDao;

    User user = new User(1, "sdfsd", "fghdgfh", "sdfdf@yandex.ru",
            LocalDate.of(2000, 1, 1));

    User userUpdate = new User(1, "Яндекс", "Практикум", "sdfdf@yandex.ru",
            LocalDate.of(2000, 1, 1));

    @Test
    void setUser() {
        userDao.save(user);
        User user1 = userDao.getUsersById(user.getId());
        assertEquals(user1.getId(), 1);
    }

    @Test
    void putUser() {
        userDao.save(user);
        userDao.update(userUpdate);
        User user1 = userDao.getUsersById(user.getId());
        assertEquals(user1.getName(), userUpdate.getName());
        assertEquals(user1.getLogin(), userUpdate.getLogin());
    }

    @Test
    void getUsersById() {
        userDao.save(user);
        User user1 = userDao.getUsersById(user.getId());
        assertEquals(user, user1);
    }

    @Test
    void getAllUsers() {
        userDao.save(user);
        User user1 = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(user1);
        List<User> users = userDao.getAllUsers();
        assertEquals(users.get(0).getId(), user.getId());
        assertEquals(users.get(1).getId(), user1.getId());
    }

    @Test
    void addFriend() {
        userDao.save(user);
        User friend = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        assertTrue(friendshipDao.getFriendsByUser(user.getId()).contains(friend.getId()));
        assertTrue(friend.getFriendsId().isEmpty());
    }

    @Test
    void removeFriend() {
        userDao.save(user);
        User friend = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        friendshipDao.removeFriend(user.getId(), friend.getId());
        assertTrue(friendshipDao.getFriendsByUser(user.getId()).isEmpty());
        assertTrue(friend.getFriendsId().isEmpty());
    }

    @Test
    void listOfFriends() {
        userDao.save(user);
        User friend = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        Set<User> friends = userDao.listOfFriends(user.getId());
        assertTrue(friends.contains(friend));
    }

    @Test
    void commonFriends() {
        userDao.save(user);
        User user1 = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(user1);
        User user2 = new User(3, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(user2);
        friendshipDao.addFriend(user.getId(), user1.getId());//1 и 2 дружат
        friendshipDao.addFriend(user1.getId(), user.getId());
        friendshipDao.addFriend(user.getId(), user2.getId());//1 и 3 дружат
        friendshipDao.addFriend(user2.getId(), user.getId());
        Set<User> commonFriends = userDao.commonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.contains(userDao.getUsersById(user.getId())));
    }

    @Test
    void getFriendshipStatus() {
        userDao.save(user);
        User user1 = new User(2, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(user1);
        User user2 = new User(3, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userDao.save(user2);
        friendshipDao.addFriend(user.getId(), user1.getId());//1 и 2 дружат
        friendshipDao.addFriend(user1.getId(), user.getId());
        friendshipDao.addFriend(user.getId(), user2.getId());//1 и дружит с 3 а 3 с 1 нет
        FriendshipStatus friendshipStatus1 = friendshipDao.getFriendshipStatus(user.getId(), user1.getId());
        assertEquals(friendshipStatus1, FriendshipStatus.CONFIRMED);
        FriendshipStatus friendshipStatus2 = friendshipDao.getFriendshipStatus(user.getId(), user2.getId());
        assertEquals(friendshipStatus2, FriendshipStatus.UNCONFIRMED);
    }
}

