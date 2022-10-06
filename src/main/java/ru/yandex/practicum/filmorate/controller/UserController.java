package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController implements Controllers<User> {

    private final UserService userService;
    private final FeedService feedService;

    @Override
    @PostMapping
    public User add(@RequestBody User newUser) {
        return userService.add(newUser);
    }

    @Override
    @PutMapping
    public User update(@RequestBody User updatedUser) {
        return userService.update(updatedUser);
    }

    @Override
    @GetMapping("{id}")
    public User getById(@PathVariable("id") int userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PutMapping("{id}/friends/{friendId}")
    public String addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public String deleteFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    @ResponseBody
    public List<User> getListOfFriends(@PathVariable("id") int userId) {
        return userService.getListOfFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    List<User> getListOfCommonFriends(@PathVariable("id") int userId, @PathVariable("otherId") int friendId) {
        return userService.getListOfCommonFriends(userId, friendId);
    }

    @GetMapping("{id}/feed")
    public List<Feed> getFeedsByUserId(@PathVariable("id") int userId) {
        return feedService.getByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable("id") int userId){
        return userService.deleteUserById(userId);
    }

    @GetMapping("{id}/recommendations")
    @ResponseBody
    public List<Film> getRecommendation(@PathVariable("id") int userId) {
        return userService.getRecommendation(userId);
    }
}
