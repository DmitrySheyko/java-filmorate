package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.Services;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController implements Controllers<Mpa> {
    private final Services<Mpa> services;

    @Override
    @GetMapping
    public List<Mpa> getAll() {
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Mpa getById(@PathVariable("id") int ratingId) {
        return services.getById(ratingId);
    }

    @Override
    @PostMapping
    public Mpa add(@RequestBody Mpa newRating) {
        return services.add(newRating);
    }

    @Override
    @PutMapping
    public Mpa update(@RequestBody Mpa mpaForUpdate) {
        return services.update(mpaForUpdate);
    }
}
