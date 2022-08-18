package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLOutput;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));
    public final FilmStorage filmStorage;
    public final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;  // getBeans
        this.userStorage = userStorage;  // getBeans
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId){
        if(filmStorage.checkIsFilmInStorage(filmId)){
            return filmStorage.getFilmById(filmId);
        } else throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", filmId));
    }

    public Film addFilm(Film newFilm) {
        if (checkIsFilmDataCorrect(newFilm)) {
            return filmStorage.addFilm(newFilm);
        } else {
            return null;
        }
    }

    public Film updateFilm(Film updatedFilm) {
        if (filmStorage.checkIsFilmInStorage(updatedFilm)) {
            if (checkIsFilmDataCorrect(updatedFilm)) {
                return filmStorage.updateFilm(updatedFilm);
            } else {
                return null;
            }
        } else {
            throw new ObjectNotFoundException(String.format("Не удалось обновить данные о фильме т.к. фильм Id=%s не найден", updatedFilm.getId()));
        }
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            throw new ValidationException(String.format("Фильм id=%f не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            throw new ValidationException(String.format("Пользователь id=%f не найден", userId));
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!filmStorage.checkIsFilmHasLikeFromUser(filmId, userId)) {
            throw new ValidationException(String.format("Для фильма id=%f лайк от пользователя id=%f не найден", filmId, userId));
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2)-> (f1.getSetOfLikes().size()-f2.getSetOfLikes().size())*(-1))
//                .sorted(Comparator.comparingInt(f -> f.getSetOfLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


    public boolean checkIsFilmDataCorrect(Film newFilm) {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.info("Не удалось добавить/обновить фильм т.к. не указано название");
            throw new ValidationException("Не указано название фильма");
        } else if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.info("Не удалось добавить/обновить фильм т.к. превышена допустимая длина описания");
            throw new ValidationException("Превышена допустимая длина описания - 200 символов");
        } else if (newFilm.getReleaseDate() == null || getInstance(newFilm.getReleaseDate())
                .isBefore(MIN_RELEASE_DATA)) {
            log.info("Не удалось добавить/обновить фильм т.к. указана некорректная дата выхода");
            throw new ValidationException("Указана некорректная дата выхода фильма");
        } else if (getDuration(newFilm.getDuration()).isNegative() || getDuration(newFilm.getDuration()).isZero()) {
            log.info("Не удалось добавить/обновить фильм т.к. некорректно указана длительность");
            throw new ValidationException("Указана некорректная длительность фильма");
        } else {
            return true;
        }
    }

    private Instant getInstance(String time) {  // TODO переделать работу со временем
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }

    private Duration getDuration(long duration) {
        return Duration.ofMinutes(duration);
    }
}
