package ru.yandex.practicum.filmorate.servise;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final HashMap<Integer, Set<Integer>> friendshipList = new HashMap<>();

    public void addFriend(Integer thisId, Integer friendId) {
        isThisIdAlreadyAddCheck(thisId);
        isThisIdAlreadyAddCheck(friendId);
        friendshipList.get(thisId).add(friendId);
        friendshipList.get(friendId).add(thisId);
    }

    public void removeFriend(Integer thisId, Integer friendId) {
        isThisIdAlreadyAddCheck(thisId);
        isThisIdAlreadyAddCheck(friendId);
        if (friendshipList.containsKey(thisId)) {
            friendshipList.get(thisId).remove(friendId);
        }
        if (friendshipList.containsKey(friendId)) {
            friendshipList.get(friendId).remove(thisId);
        }
    }

    public Set<Integer> commonFriends(Integer thisId, Integer friendId) {

        Set<Integer> commonFriends = new HashSet<>();
        isThisIdAlreadyAddCheck(thisId);
        isThisIdAlreadyAddCheck(friendId);
        if (!friendshipList.get(thisId).isEmpty()) {
            for (Integer id : friendshipList.get(thisId)) {
                if (friendshipList.get(friendId).contains(id)) {
                    commonFriends.add(id);
                }
            }
            return commonFriends;
        } else {
            return Collections.emptySet();
        }

    }

    public Set<Integer> listOfFriends(Integer userId) {
        return friendshipList.getOrDefault(userId, null);
    }

    private void isThisIdAlreadyAddCheck(Integer thisId) {
        if (!friendshipList.containsKey(thisId)) {
            friendshipList.put(thisId, new HashSet<>());
        }
    }


}
