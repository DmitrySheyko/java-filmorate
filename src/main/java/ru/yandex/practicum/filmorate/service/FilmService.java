package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));
    public final FilmStorage filmStorage;

    @Autowired
    public FilmService() {
        this.filmStorage = new InMemoryFilmStorage();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film newFilm) throws ValidationException {
        if (checkIsFilmDataCorrect(newFilm)) {
            return filmStorage.addFilm(newFilm);
        } else {
            return null;
        }
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException {
        if (filmStorage.checkIsFilmInStorage(updatedFilm)) {
            if (checkIsFilmDataCorrect(updatedFilm)) {
                return filmStorage.addFilm(updatedFilm);
            } else {
                return null;
            }
        } else {
            throw new ValidationException("Не удалось обновить данные т.к. фильм не найден");
        }
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) throws ValidationException {
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
