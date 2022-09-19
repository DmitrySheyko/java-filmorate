package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController implements Controllers<Film>{
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @GetMapping
    public List<Film> getAll() {
        log.info("Получен запрос на получение списка фильмов");
        return filmService.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Film getById(@PathVariable("id") int filmId) {
        log.info("Получен запрос на получение фильма id={}", filmId);
        return filmService.getById(filmId);
    }

    @Override
    @PostMapping
    public Film add(@RequestBody Film newFilm) {
        log.info("Получен запрос на добавление нового фильма");
        return filmService.add(newFilm);
    }

    @Override
    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        log.info("Получен запрос на обновление фильма id={}", updatedFilm.getId());
        return filmService.update(updatedFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка фильму id={} от пользователя id={}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка фильму id={} от пользователя id={}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10")
                                      @Positive(message = "Количество фиильмов в списке должно быть положительным")
                                      int count) {
        log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков", count);
        return filmService.getPopularFilms(count);
    }
}
