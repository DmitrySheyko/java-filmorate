package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
    public UserController() {
        this.userService = new UserService();
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) throws ValidationException {
        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) throws ValidationException {
        return userService.updateUser(updatedUser);
    }
}
