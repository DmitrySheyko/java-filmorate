package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }  // TODO getBeans

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int userId){
        if(userStorage.checkIsUserInStorage(userId)){
            return userStorage.getUserById(userId);
        } else throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
    }

    public User addUser(User newUser) {
        if (checkIsUserDataCorrect(newUser)) {
            return userStorage.addUser(newUser);
        } else return null;
    }

    public User updateUser(User updatedUser) {
        if (userStorage.checkIsUserInStorage(updatedUser.getId())) {
            if (checkIsUserDataCorrect(updatedUser)) {
                return userStorage.updateUser(updatedUser);
            } else return null;
        } else {
            throw new ObjectNotFoundException(String.format("Не удалось обновить данные т.к. пользователь id=%s не найден", updatedUser.getId()));
        }
    }

    public boolean checkIsUserDataCorrect(User newUser) {
        if (newUser.getEmail() == null || (!newUser.getEmail().contains("@")) || newUser.getEmail().isBlank()) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указан email");
            throw new ValidationException("Указан некорректный email");
        } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указан login");
            throw new ValidationException("Некорректно указан login");
        } else if (newUser.getBirthday() == null || getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указана дата рождения");
            throw new ValidationException("Некорректно указана дата рождения");
        }
        if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
            return true;
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("Пользователь id={} не найден", friendId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", friendId));
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ValidationException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("Пользователь id={} не найден", friendId);
            throw new ValidationException(String.format("Пользователь id=%s не найден", friendId));
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListOfFriends(int userId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        return userStorage.getListOfFriends(userId);
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("Пользователь id={} не найден", friendId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", friendId));
        }
        ArrayList<User> resultList = new ArrayList<>(userStorage.getListOfFriends(userId));
        resultList.retainAll(userStorage.getListOfFriends(friendId));
        return resultList;
    }
}
