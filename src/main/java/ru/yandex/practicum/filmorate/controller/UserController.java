package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на добавление нового пользователя");
        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Получен запрос на обновление данных пользователя id={}", updatedUser.getId());
        return userService.updateUser(updatedUser);
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") int userId) {
        log.info("Получен запрос на получение пользователя id={}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userService.getAllUsers();
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Получен запрос на добавление польхователя id={} в друзья пользователю id={}", friendId, userId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос на удаление польхователя id={} из друзей пользователя id={}", friendId, userId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    @ResponseBody
    public List<User> getListOfFriends(@PathVariable("id") int userId) {
        log.info("Получен запрос на получения списка друзей пользователя id={}", userId);
        return userService.getListOfFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    List<User> getListOfCommonFriends(@PathVariable("id") int userId, @PathVariable("otherId") int friendId) {
        log.info("Получен запрос на получение общего списка друзей пользователей id={} и id={}", userId, friendId);
        return userService.getListOfCommonFriends(userId, friendId);
    }
}
