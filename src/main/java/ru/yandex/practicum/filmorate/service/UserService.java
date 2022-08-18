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
    private final UserStorage userStorage;
    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) {
        if (userStorage.checkIsUserInStorage(userId)) {
            return userStorage.getUserById(userId);
        } else {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
    }

    public User addUser(User newUser) {
        if (checkIsUserDataCorrect(newUser)) {
            return userStorage.addUser(newUser);
        } else return null;
    }

    public User updateUser(User updatedUser) {
        if (userStorage.checkIsUserInStorage(updatedUser)) {
            if (checkIsUserDataCorrect(updatedUser)) {
                return userStorage.updateUser(updatedUser);
            } else return null;
        } else {
            log.info("Пользователь id={} не найден", updatedUser.getId());
            throw new ObjectNotFoundException(String.format("Не удалось обновить данные пользователя id=%s т.к. " +
                    "пользователь не найден", updatedUser.getId()));
        }
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
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("Пользователь id={} не найден", friendId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", friendId));
        }
        if (!userStorage.checkAreTheseUsersFriends(userId, friendId)) {
            log.info("Пользователь id={} не в списке друзей пользователя id={}", friendId, userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не в списке друзей пользователя id=%s",
                    friendId, userId));
        }
        if (!userStorage.checkAreTheseUsersFriends(friendId, userId)) {
            log.info("Пользователь id={} не в списке друзей пользователя id={}", userId, friendId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не в списке друзей пользователя id=%s",
                    userId, friendId));
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
        log.info("Направлен общий список друзей пользователей id={} и id={}", userId, friendId);
        return resultList;
    }

    public boolean checkIsUserDataCorrect(User newUser) {
        if (newUser.getEmail() == null || (!newUser.getEmail().contains("@")) || newUser.getEmail().isBlank()) {
            log.info("Указан некорректный email");
            throw new ValidationException("Указан некорректный email");
        } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.info("Указан некорректный login");
            throw new ValidationException("Указан некорректный login");
        } else if (newUser.getBirthday() == null || getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.info("Указана некорректная дата рождения");
            throw new ValidationException("Указана некорректная дата рождения");
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
}
