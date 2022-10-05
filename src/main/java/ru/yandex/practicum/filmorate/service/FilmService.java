package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FilmService implements Services<Film> {

    private final FilmDbStorage filmDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final DateTimeFormatter dateTimeFormatter;
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmDbStorage filmDbStorage, FeedDbStorage feedDbStorage) {
        this.filmDbStorage = filmDbStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.feedDbStorage = feedDbStorage;
    }

    @Override
    public List<Film> getAll() {
        return filmDbStorage.getAll();
    }

    @Override
    public Film getById(int filmId) {
        return filmDbStorage.getById(filmId);
    }

    @Override
    public Film add(Film newFilm) {
        if (checkIsFilmDataCorrect(newFilm)) {
            return filmDbStorage.add(newFilm);
        }
        return null;
    }

    @Override
    public Film update(Film updatedFilm) {
        if (checkIsFilmDataCorrect(updatedFilm)) {
            return filmDbStorage.update(updatedFilm);
        }
        return null;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmDbStorage.addLike(filmId, userId);
        feedDbStorage.add(filmId, FeedService.eventTypeLike, FeedService.operationAdd, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmDbStorage.deleteLike(filmId, userId);
        feedDbStorage.add(filmId, FeedService.eventTypeLike, FeedService.operationRemove, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
    }

    public List<Film> getPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmDbStorage.getFilmsByDirector(directorId, sortBy);
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
