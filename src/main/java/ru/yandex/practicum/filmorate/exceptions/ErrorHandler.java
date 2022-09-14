package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
//import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;


import javax.validation.ConstraintViolationException;

@RestControllerAdvice(assignableTypes = {
//        FilmController.class,
//        UserController.class,
//        UserService.class,
//        FilmDbStorage.class,
//        UserDbStorage.class
        //,
//        FilmService.class,
 //       InMemoryFilmStorage.class,
//        InMemoryUserStorage.class
        })
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String handlerOfValidationException(final ValidationException e) {
        return String.format("Ошибка. %s", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handlerOfMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return String.format("Ошибка. %s", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handlerOfConstraintViolationException(final ConstraintViolationException e) {
        return String.format("Ошибка. %s", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public String handlerOfObjectNotFoundException(final ObjectNotFoundException e) {
        return String.format("Ошибка. %s", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlerExceptions(final Exception e) {
        return String.format("Ошибка. %s", e.getMessage());
    }
}
