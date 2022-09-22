package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Set;

public interface FriendshipStorage {

    Set<Integer> getFriendsByUser(Integer id);

    String addFriend(Integer userId, Integer friendId);

    String removeFriend(Integer userId, Integer friendId);


    FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId);

}
