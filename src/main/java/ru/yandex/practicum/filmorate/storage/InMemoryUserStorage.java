package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(int userId){
        return users.get(userId);
    }

    public User addUser(User newUser) {
        newUser.generateAndSetId();
        newUser.generateSetOfFriends();
        users.put(newUser.getId(), newUser);
        log.info("Добавлен новый пользователь id={}", newUser.getId());
        return newUser;
    }

    public void addFriend(int userId, int friendId){
        users.get(userId).addFriend(friendId);
        log.info("Пользователю id={} добавлен новый друг id={}", userId, friendId);
        users.get(friendId).addFriend(userId);
        log.info("Пользователю id={} добавлен новый друг id={}", friendId, userId);
    }

    public void deleteFriend(int userId, int friendId){
        users.get(friendId).deleteFriend(friendId);
        log.info("Из списка друзей пользователя id={} удвлен друг id={}", userId, friendId);
    }

    public List<User> getListOfFriends(int userId){
        return users.get(userId).getSetOfFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public User updateUser(User updatedUser){
        // Добавляем обновленной версии пользователя список друзей от старой версии пользователя
        updatedUser.setSetOfFriends(users.get(updatedUser.getId()).getSetOfFriends());
        users.put(updatedUser.getId(), updatedUser);
        log.info("Данные пользователя id = {} обновлены", updatedUser.getId());
        return updatedUser;
    }

    public boolean checkIsUserInStorage(User user){
        return users.containsValue(user);
    }

    public boolean checkIsUserInStorage(int userId){
        return users.containsKey(userId);
    }
}
