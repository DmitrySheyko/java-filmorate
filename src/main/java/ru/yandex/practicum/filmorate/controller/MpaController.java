package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAllMpaRatings() {
        log.info("Получен запрос на получение списка рейтингов");
        return mpaService.getAllMpaRatings();
    }

    @GetMapping("{id}")
    public Mpa getMpaRatingById(@PathVariable("id") int ratingId) {
        log.info("Получен запрос на получение названия рейтинга id={}", ratingId);
        return mpaService.getMpaRatingById(ratingId);
    }
}
