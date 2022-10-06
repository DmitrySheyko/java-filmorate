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
    private final Services<Genre> services;

    @Override
    @GetMapping
    public List<Genre> getAll() {
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Genre getById(@PathVariable("id") int genreId) {
        return services.getById(genreId);
    }

    @Override
    @PostMapping
    public Genre add(@RequestBody Genre newGenre) {
        return services.add(newGenre);
    }

    @Override
    @PutMapping
    public Genre update(@RequestBody Genre genreForUpdate) {
        return services.update(genreForUpdate);
    }
}
