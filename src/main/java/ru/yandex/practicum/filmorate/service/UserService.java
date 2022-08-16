package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final UserStorage userStorage;

    public UserService() {
        this.userStorage = new InMemoryUserStorage();
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User newUser) throws ValidationException {
        if (checkIsUserDataCorrect(newUser)) {
            return userStorage.addUser(newUser);
        } else return null;
    }

    public User updateUser(User updatedUser) throws ValidationException {
        if (checkIsUserDataCorrect(updatedUser)) {
            if (userStorage.checkIsUserInStorage(updatedUser)) {
                return userStorage.updateUser(updatedUser);
            } else {
                throw new ValidationException("Не удалось обновить данные т.к. пользователь id=" + updatedUser.getId() +
                        " не найден");
            }
        }
        return null;
    }

    public boolean checkIsUserDataCorrect(User newUser) throws ValidationException {
        if (newUser.getEmail() == null || (!newUser.getEmail().contains("@")) || newUser.getEmail().isBlank()) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указан email");
            throw new ValidationException("Указан некорректный email");
        } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указан login");
            throw new ValidationException("Некорректно указан login");
        } else if (newUser.getBirthday() == null || getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.info("Не удалось добавить/обновать пользователя т.к. некорректно указана дата рождения");
            throw new ValidationException("Некорректно указана дата рождения");
        } else if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return true;
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }
}
