package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DateTimeFormatter dateTimeFormatter;
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film addFilm(Film newFilm) {
        if (checkIsFilmDataCorrect(newFilm)) {
            return filmStorage.addFilm(newFilm);
        }
        return null;
    }

    public Film updateFilm(Film updatedFilm) {
        if (checkIsFilmDataCorrect(updatedFilm)) {
            return filmStorage.updateFilm(updatedFilm);
        }
        return null;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) {
        if (getInstance(newFilm.getReleaseDate()).isBefore(MIN_RELEASE_DATA)) {
            log.info("Указана некорректная дата выхода фильма");
            throw new ValidationException(String.format("Указана некорректная дата выхода фильма. Требуется дата" +
                    " не ранее %s", MIN_RELEASE_DATA));
        } else {
            return true;
        }
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }
}
