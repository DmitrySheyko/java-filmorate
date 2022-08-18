package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(int userId);

    User addUser(User newUser);

    void addFriend(int userId,int friendId);

    User updateUser(User updatedUser);

    boolean checkIsUserInStorage(User user);

    boolean checkIsUserInStorage(int userId);

    void deleteFriend(int userId, int friendId);

    List<User> getListOfFriends(int userId);

    boolean checkAreTheseUsersFriends(Integer userId, Integer friendId);
}
