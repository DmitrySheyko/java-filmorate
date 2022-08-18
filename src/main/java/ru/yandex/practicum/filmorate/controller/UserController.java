package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById (@PathVariable("id") int userId){
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {
        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser){
        return userService.updateUser(updatedUser);
    }

    // добавление в друзья  //TODO проверить на добавление уже добавленного друга
    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        userService.addFriend(userId, friendId);
    }

    // удаление из друзей
    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    // получение списка друзей
    @GetMapping("{id}/friends")
    @ResponseBody
    public List<User> getListOfFriends(@PathVariable("id") int userId) {
        return userService.getListOfFriends(userId);
    }

    // получение списка общих друзей
    @GetMapping("{id}/friends/common/{otherId}")
    List<User> getListOfCommonFriends(@PathVariable("id") int userId, @PathVariable("otherId") int friendId) {
        return userService.getListOfCommonFriends(userId, friendId);
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler (ObjectNotFoundException.class)
//    @ResponseBody
//    public Map<String, String> handlerOfObjectNotFoundException(final ObjectNotFoundException e) {
//        return Map.of("Error", e.getMessage());
//    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler (ValidationException.class)
//    public ErrorResponse handlerOfValidationException(final ValidationException e) {
//        return new ErrorResponse(e.getMessage());
//    }
//
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler (ObjectNotFoundException.class)
//    @ResponseBody
//    public Map<String, String> handlerOfObjectNotFoundException(final ObjectNotFoundException e) {
//        return Map.of("Error", e.getMessage());
//    }
}
