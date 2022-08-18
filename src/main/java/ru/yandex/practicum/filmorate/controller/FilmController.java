package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable("id") int filmId){
        log.info("Получен запрос на получение фильма id={}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на добавление нового фильма");
        return filmService.addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Получен запрос на обновление фильма id={}", updatedFilm.getId());
        return filmService.updateFilm(updatedFilm);
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение списка из {} фильмов с наибольшим количеством лайков", count);
        return filmService.getPopularFilms(count);
    }
}
