package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UserService implements Services<User> {
    private final UserDbStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public UserService(UserDbStorage userStorage, FeedDbStorage feedDbStorage) {
        this.userStorage = userStorage;
        this.feedDbStorage = feedDbStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int userId) {
        return userStorage.getById(userId);
    }

    public User add(User newUser) {
        if (checkIsUserDataCorrect(newUser)) {
            return userStorage.add(newUser);
        } else return null;
    }

    public User update(User updatedUser) {
        if (checkIsUserDataCorrect(updatedUser)) {
            return userStorage.update(updatedUser);
        } else return null;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
        feedDbStorage.add(friendId, FeedService.eventTypeFriend, FeedService.operationAdd, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        feedDbStorage.add(friendId, FeedService.eventTypeFriend, FeedService.operationRemove, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
    }

    public List<User> getListOfFriends(int userId) {
        return userStorage.getListOfFriends(userId);
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        return userStorage.getListOfCommonFriends(userId, friendId);
    }

    public boolean checkIsUserDataCorrect(User newUser) {
        if (newUser.getLogin().contains(" ")) {
            log.info("Указан некорректный login");
            throw new ValidationException("Указан некорректный login");
        } else if (getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
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

    public String deleteUserById (Integer userId){
        if (! userStorage.checkIsObjectInStorage(userId)){
            String message = "Пользователь user_id=" + userId+" не найден.";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        String message = userStorage.deleteUserById(userId);
        log.info(message);
        return message;
    }
}
