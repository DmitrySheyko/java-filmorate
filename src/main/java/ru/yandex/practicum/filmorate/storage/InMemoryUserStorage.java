package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public User addUser(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("Добавлен новый пользователь id={}", newUser.getId());
        return newUser;
    }

    public User updateUser(User updatedUser){
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
