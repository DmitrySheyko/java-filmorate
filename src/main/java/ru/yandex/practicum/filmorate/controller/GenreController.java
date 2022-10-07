package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.Services;

import java.util.List;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController implements Controllers<Genre> {
    private final Services<Genre> service;

    @Override
    @GetMapping
    public List<Genre> getAll() {
        return service.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Genre getById(@PathVariable("id") int genreId) {
        return service.getById(genreId);
    }

    @Override
    @PostMapping
    public Genre add(@RequestBody Genre newGenre) {
        return service.add(newGenre);
    }

    @Override
    @PutMapping
    public Genre update(@RequestBody Genre genreForUpdate) {
        return service.update(genreForUpdate);
    }
}
