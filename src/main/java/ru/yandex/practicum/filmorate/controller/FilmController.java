package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController implements Controllers<Film> {
    private final FilmService filmService;

    @Override
    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Film getById(@PathVariable("id") int filmId) {
        return filmService.getById(filmId);
    }

    @Override
    @PostMapping
    public Film add(@RequestBody Film newFilm) {
        return filmService.add(newFilm);
    }

    @Override
    @PutMapping
    public Film update(@RequestBody Film filmForUpdate) {
        return filmService.update(filmForUpdate);
    }

    @PutMapping("{id}/like/{userId}")
    public String addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public String deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10")
                                      @Positive(message = "Количество фильмов в списке должно быть положительным")
                                      int count,
                                      @RequestParam(defaultValue = "0")
                                      int genreId,
                                      @RequestParam(defaultValue = "0")
                                      int year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("search")
    public List<Film> searchFilmByNameOrDirector(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.searchFilmByNameOrDirector(query, by);
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable("id") int filmId) {
        return filmService.deleteFilmById(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}

