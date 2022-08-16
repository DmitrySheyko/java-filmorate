package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice (value = "ru/yandex/practicum/filmorate")
public class ErrorHandler {

    @ExceptionHandler
    public void handler(){

    }
}
