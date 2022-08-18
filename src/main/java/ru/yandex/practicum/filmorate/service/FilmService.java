package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    public final FilmStorage filmStorage;
    public final UserStorage userStorage;
    private final DateTimeFormatter dateTimeFormatter;
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));


    @Autowired
    public FilmService(FilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        if (filmStorage.checkIsFilmInStorage(filmId)) {
            return filmStorage.getFilmById(filmId);
        } else {
            log.info("Фильм id={} не найден", filmId);
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", filmId));
        }
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
            log.info("Фильм id={} не найден", updatedFilm);
            throw new ObjectNotFoundException(String.format("Не удалось обновить данные о фильме id=%s т.к. фильм  не найден", updatedFilm.getId()));
        }
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            log.info("Фильм id={} не найден", filmId);
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            log.info("Фильм id={} не найден", filmId);
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("Пользователь id={} не найден", userId);
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден", userId));
        }
        if (!filmStorage.checkIsFilmHasLikeFromUser(filmId, userId)) {
            log.info("Для фильма id={} лайк от пользователя id={} не найден", filmId, userId);
            throw new ValidationException(String.format("Для фильма id=%s лайк от пользователя id=%s не найден",
                    filmId, userId));
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Направлен список из {} фильмов с наибольшим количеством лайков", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> (f1.getSetOfLikes().size() - f2.getSetOfLikes().size()) * (-1))
                .limit(count)
                .collect(Collectors.toList());
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.info("Не указано название фильма");
            throw new ValidationException("Не указано название фильма");
        } else if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.info("Превышена допустимая длина описания - 200 символов");
            throw new ValidationException("Превышена допустимая длина описания - 200 символов");
        } else if (newFilm.getReleaseDate() == null || getInstance(newFilm.getReleaseDate())
                .isBefore(MIN_RELEASE_DATA)) {
            log.info("Указана некорректная дата выхода фильма");
            throw new ValidationException(String.format("Указана некорректная дата выхода фильма. Требуется дата" +
                    " не ранее %s", MIN_RELEASE_DATA.toString()));
        } else if (getDuration(newFilm.getDuration()).isNegative() || getDuration(newFilm.getDuration()).isZero()) {
            log.info("Указана некорректная длительность фильма. Требуется длительность более 0 минут.");
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
