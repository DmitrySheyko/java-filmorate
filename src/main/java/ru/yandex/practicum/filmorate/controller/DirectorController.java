package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController implements Controllers<Director> {

    private final DirectorService services;

    @Override
    @GetMapping
    public List<Director> getAll() {
        log.info("Получен запрос на получение списка режиссеров");
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Director getById(@PathVariable("id") int directorId) {
        log.info("Получен запрос на получение режиссера id={}", directorId);
        return services.getById(directorId);
    }

    @Override
    @PostMapping
    public Director add(@Valid @RequestBody Director newDirector) {
        log.info("Получен запрос на создание режиссера name={}", newDirector.getName());
        return services.add(newDirector);
    }

    @Override
    @PutMapping
    public Director update(@Valid @RequestBody Director updatedDirector) {
        log.info("Получен запрос на обновление данных режиссера id={}", updatedDirector.getId());
        return services.update(updatedDirector);
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable("id") int directorId) {
        log.info("Получен запрос на удаление режиссера с id={}", directorId);
        services.deleteDirector(directorId);
    }
}
