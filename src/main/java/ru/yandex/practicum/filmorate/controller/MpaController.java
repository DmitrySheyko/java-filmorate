package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.Services;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController implements Controllers<Mpa> {
    private final Services<Mpa> services;

    @Override
    @GetMapping
    public List<Mpa> getAll() {
        log.info("Получен запрос на получение списка рейтингов");
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Mpa getById(@PathVariable("id") int ratingId) {
        log.info("Получен запрос на получение названия рейтинга id={}", ratingId);
        return services.getById(ratingId);
    }

    @Override
    @PostMapping
    public Mpa add(@RequestBody Mpa newRating) {
        log.info("Получен запрос на создание рейтинга name={}", newRating.getName());
        return services.add(newRating);
    }

    @Override
    @PutMapping
    public Mpa update(@RequestBody Mpa updatedRating) {
        log.info("Получен запрос на обновление рейтинга id={}", updatedRating.getId());
        return services.update(updatedRating);
    }
}
