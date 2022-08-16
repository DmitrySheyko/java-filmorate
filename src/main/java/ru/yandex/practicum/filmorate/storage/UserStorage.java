package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User addUser(User newUser);

    User updateUser(User updatedUser);

    boolean checkIsUserInStorage(User user);

    boolean checkIsUserInStorage(int userId);
}
