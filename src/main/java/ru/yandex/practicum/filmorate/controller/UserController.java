package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController implements Controllers<User> {

    private final UserService userService;
    private final FeedService feedService;

    @Override
    @PostMapping
    public User add(@RequestBody User newUser) {
        log.info("Получен запрос на добавление нового пользователя");
        return userService.add(newUser);
    }

    @Override
    @PutMapping
    public User update(@RequestBody User updatedUser) {
        log.info("Получен запрос на обновление данных пользователя id={}", updatedUser.getId());
        return userService.update(updatedUser);
    }

    @Override
    @GetMapping("{id}")
    public User getById(@PathVariable("id") int userId) {
        log.info("Получен запрос на получение пользователя id={}", userId);
        return userService.getById(userId);
    }

    @Override
    @GetMapping
    public List<User> getAll() {
        log.info("Получен запрос на получение списка пользователей");
        return userService.getAll();
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

    @GetMapping("{id}/feed")
    public List<Feed> getFeedsByUserId(@PathVariable("id") int userId) {
        return feedService.getByUserId(userId);
    }

    // Дмимтрий add-recommendation
    @GetMapping("{id}/recommendations")
    @ResponseBody
    public List<Film> getRecommendation(@PathVariable("id") int userId) {
        log.info("Получен запрос на получения списка рекоменаций для пользователя id={}", userId);
        return userService.getRecommendation(userId);
    }
}
