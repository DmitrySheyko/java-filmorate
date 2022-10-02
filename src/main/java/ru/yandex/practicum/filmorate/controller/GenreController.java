package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.Services;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController implements Controllers<Genre> {
    private final Services<Genre> services;

    @Override
    @GetMapping
    public List<Genre> getAll() {
        log.info("Получен запрос на получение списка жанров");
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Genre getById(@PathVariable("id") int genreId) {
        log.info("Получен запрос на получение жанра id={}", genreId);
        return services.getById(genreId);
    }

    @Override
    @PostMapping
    public Genre add(@RequestBody Genre newGenre) {
        log.info("Получен запрос на создание жанра name={}", newGenre.getName());
        return services.add(newGenre);
    }

    @Override
    @PutMapping
    public Genre update(@RequestBody Genre updatedGenre) {
        log.info("Получен запрос на обновление жанра id={}", updatedGenre.getId());
        return services.update(updatedGenre);
    }
}
