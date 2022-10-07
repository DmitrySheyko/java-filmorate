package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
        try {
            List<User> users = userStorage.getAll();
            log.info("Получен список всех пользователей");
            return users;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public User getById(int userId) {
        try {
            User user = userStorage.getById(userId);
            log.info("Получен пользователь user_id=" + userId + ".");
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public User add(User newUser) {
        if (!checkIsUserDataCorrect(newUser)) {
            String message = "Данные пользователя user_id=" + newUser.getId() + " содержат некорректную информцию.";
            log.error(message);
            throw new ValidationException(message);
        }
        userStorage.add(newUser);
        log.info("Пользователь user_id=" + newUser.getId() + " успешно добавлен.");
        return newUser;
    }

    public User update(User userForUpdate) {
        try {
            if (!checkIsUserDataCorrect(userForUpdate)) {
                String message = "Данные пользователя user_id=" + userForUpdate.getId() + " содержат некорректную информцию.";
                log.error(message);
                throw new ValidationException(message);
            }
            if (!userStorage.checkIsObjectInStorage(userForUpdate)) {
                String message = "Пользователь user_id=" + userForUpdate + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            userStorage.update(userForUpdate);
            log.info("Пользователь user_id=" + userForUpdate.getId() + " успешно обновлен.");
            return userForUpdate;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Пользователь user_id=" + userForUpdate.getId() + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public String addFriend(Integer userId, Integer friendId) {
        try {
            if (!userStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            if (!userStorage.checkIsObjectInStorage(friendId)) {
                String message = "Пользователь user_id=" + friendId + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            if (userId == friendId) {
                String message = "Добавление в друзья самого себя не поддерживается.";
                log.error(message);
                throw new ValidationException(message);
            }
            String message = userStorage.addFriend(userId, friendId);
            log.info(message);
            feedDbStorage.add(friendId, FeedService.eventTypeFriend, FeedService.operationAdd, userId);
            log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
            return message;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Пользователь user_id=" + friendId
                    + " уже был добавлен в друзья пользователю user_id=" + friendId + ".";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    public String deleteFriend(Integer userId, Integer friendId) {
        try {
            if (!userStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            if (!userStorage.checkIsObjectInStorage(friendId)) {
                String message = "Пользователь user_id=" + friendId + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            String message = userStorage.deleteFriend(userId, friendId);
            log.info(message);
            feedDbStorage.add(friendId, FeedService.eventTypeFriend, FeedService.operationRemove, userId);
            log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
            return message;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Пользователь user_id=" + friendId
                    + " не найден в спискедрузей пользователя user_id=" + friendId + ".";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public List<User> getListOfFriends(int userId) {
        try {
            if (!userStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            List<User> friends = userStorage.getListOfFriends(userId);
            log.info("Получен список друзей пользователя user_id=" + userId + ".");
            return friends;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        try {
            List<User> commonFriends = userStorage.getListOfCommonFriends(userId, friendId);
            log.info("Получен список общих друзей пользователей user_id=" + userId + " и user_id=" + friendId + ".");
            return commonFriends;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public boolean checkIsUserDataCorrect(User newUser) {
        if (newUser.getLogin().contains(" ")) {
            log.error("Указан некорректный login");
            throw new ValidationException("Указан некорректный login");
        } else if (getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.error("Указана некорректная дата рождения");
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

    public String deleteUserById(Integer userId) {
        if (!userStorage.checkIsObjectInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " не найден.";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        String message = userStorage.deleteUserById(userId);
        log.info(message);
        return message;
    }

    public List<Film> getRecommendation(int userId) {
        try {
            if (!userStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
                log.error(message);
                throw new ValidationException(message);
            }
            List<Film> recommendations = userStorage.getRecommendation(userId);
            log.info("Получен список рекоммендаций для пользователя user_id=" + userId + ".");
            return recommendations;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }
}

