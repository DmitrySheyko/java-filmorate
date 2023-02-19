package ru.yandex.practicum.filmorate.exceptions;

/**
 * Class of exception {@link ValidationException}.
 *
 * @author DmitrySheyko
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
