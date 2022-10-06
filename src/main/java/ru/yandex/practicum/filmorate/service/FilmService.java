package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FilmService implements Services<Film> {

    private final FilmDbStorage filmDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final UserDbStorage userDbStorage;
    private final DateTimeFormatter dateTimeFormatter;
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmDbStorage filmDbStorage, UserDbStorage userDbStorage,
                       FeedDbStorage feedDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.feedDbStorage = feedDbStorage;
    }

    @Override
    public List<Film> getAll() {
        try {
            List<Film> films = filmDbStorage.getAll();
            log.info("Получен список всех фильмов");
            return films;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Film getById(int filmId) {
        try {
            Film film = filmDbStorage.getById(filmId);
            log.info("Получен фильм film_id=" + filmId + ".");
            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Фильм film_id=" + filmId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    @Override
    public Film add(Film newFilm) {
        if (!checkIsFilmDataCorrect(newFilm)) {
            String message = "Данные фильма film_id=" + newFilm.getId() + " содержат некорректную информцию.";
            log.error(message);
            throw new ValidationException(message);
        }
        filmDbStorage.add(newFilm);
        log.info("Фильм film_id=" + newFilm.getId() + " успешно добавлен.");
        return newFilm;
    }

    @Override
    public Film update(Film filmForUpdate) {
        try {
            if (!checkIsFilmDataCorrect(filmForUpdate)) {
                String message = "Описание фильма film_id=" + filmForUpdate.getId() + " содердит некорректные данные.";
                log.error(message);
                throw new ValidationException(message);
            }
            if (!userDbStorage.checkIsObjectInStorage(filmForUpdate.getId())) {
                String message = "Пользователь user_id=" + filmForUpdate.getId() + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            filmDbStorage.update(filmForUpdate);
            log.info("Фильм film_id=" + filmForUpdate.getId() + " успешно обновлен.");
            return filmForUpdate;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Фильм film_id=" + filmForUpdate.getId() + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public String addLike(Integer filmId, Integer userId) {
        if (!filmDbStorage.checkIsObjectInStorage(filmId)) {
            String message = "Фильм film_id=" + filmId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!userDbStorage.checkIsObjectInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (filmDbStorage.checkIsFilmHasLikeFromUser(filmId, userId)) {
            String message = "Пользователь user_id=" + userId + " уже поставил лайк фильму film_id=" + filmId + ".";
            log.error(message);
            return message;
        }
        String message = filmDbStorage.addLike(filmId, userId);
        log.info(message);
        feedDbStorage.add(filmId, FeedService.eventTypeLike, FeedService.operationAdd, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
        return message;
    }

    public String deleteLike(Integer filmId, Integer userId) {
        if (!filmDbStorage.checkIsObjectInStorage(filmId)) {
            String message = "Фильм film_id=" + filmId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!userDbStorage.checkIsObjectInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!filmDbStorage.checkIsFilmHasLikeFromUser(filmId, userId)) {
            String message = "Пользователь user_id=" + userId + " уже поставил лайк фильму film_id=" + filmId + ".";
            log.error(message);
            throw new ValidationException(message);
        }
        String message = filmDbStorage.deleteLike(filmId, userId);
        log.info(message);
        feedDbStorage.add(filmId, FeedService.eventTypeLike, FeedService.operationRemove, userId);
        log.info("Лента событий пользователя user_id=" + userId + " была обновлена.");
        return message;
    }

    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId != 0 && year != 0) {
            log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков с " +
                    "сортировкой по жанру c id {} и году выхода фильма {}", count, genreId, year);
            return filmDbStorage.getPopularFilmSortedByGenreAndYear(count, genreId, year);
        }
        if (genreId != 0 && year == 0) {
            log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков с " +
                    "сортировкой по жанру c id {}", count, genreId);
            return filmDbStorage.getPopularFilmSortedByGenre(count, genreId);
        }
        if (genreId == 0 && year != 0) {
            log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков с " +
                    "сортировкой по году выхода фильма {}", count, year);
            return filmDbStorage.getPopularFilmSortedByYear(count, year);
        }
        log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков", count);
        return filmDbStorage.getPopularFilms(count);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (sortBy.equals("year") || sortBy.equals("likes")) {
            log.info("Сервис: Получен запрос на получение списка фильмов по режиссеру: {} с сортировкой по: {}",
                    directorId, sortBy);
            return filmDbStorage.getFilmsByDirector(directorId, sortBy);
        } else {
            log.error("Сервис: Получен некорректный запрос на получение списка фильмов по режиссеру: {} " +
                    "с сортировкой по: {}. " +
                    "Такие параметры не поддерживаются", directorId, sortBy);
            return Collections.emptyList();
        }
    }

    public List<Film> searchFilmByNameOrDirector(String query, List<String> by) {
        if (by.contains("director") || by.contains("title")) {
            log.info("Получен запрос на поиск фильмов по строке: {} в следующих полях: {}", query, by);
            return filmDbStorage.searchFilmByNameOrDirector(query, by);
        } else {
            log.error("Получен некорректный запрос на поиск фильмов. Параметры: {} не поддерживаются сервисом. ", by);
            return Collections.emptyList();
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        try {
            if (!filmDbStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + " не найден.";
                log.warn(message);
                throw new ObjectNotFoundException(message);
            }
            if (!filmDbStorage.checkIsObjectInStorage(friendId)) {
                String message = "Пользователь user_id=" + userId + " не найден.";
                log.warn(message);
                throw new ObjectNotFoundException(message);
            }
            List<Film> commonFilms = filmDbStorage.getCommonFilms(userId, friendId);
            log.info("Получен список общих фильмов пользователей user_id=" + userId + " и user_id=" +
                    friendId + ".");
            return commonFilms;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) {
        if (getInstance(newFilm.getReleaseDate()).isBefore(MIN_RELEASE_DATA)) {
            log.error("Указана некорректная дата выхода фильма");
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

    public String deleteFilmById(Integer filmId) {
        if (!filmDbStorage.checkIsObjectInStorage(filmId)) {
            String message = "Фильм film_id=" + filmId + " не найден.";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        String message = filmDbStorage.deleteFilmById(filmId);
        log.info(message);
        return message;
    }
}
