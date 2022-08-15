package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) throws ValidationException {
        if (checkIsUserDataCorrect(newUser)) {
            users.put(newUser.getId(), newUser);
        }
        log.info("Добавлен новый пользователь id={}", newUser.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) throws ValidationException {
        if (checkIsUserDataCorrect(newUser)) {
            if (users.containsKey(newUser.getId())) {
                users.put(newUser.getId(), newUser);
                log.info("Данные пользователя id = {} обновлены", newUser.getId());
            } else {
                throw new ValidationException("Не удалось обновить данные т.к. пользователь id=" + newUser.getId() +
                        " не найден");
            }
        }
        return newUser;
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
