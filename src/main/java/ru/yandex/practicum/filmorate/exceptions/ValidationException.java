package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Throwable{
    public ValidationException(String message) {
        super(message);
    }
}
