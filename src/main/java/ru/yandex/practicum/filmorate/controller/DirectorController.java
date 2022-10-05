package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController implements Controllers<Director> {

    private final DirectorService services;

    @Override
    @GetMapping
    public List<Director> getAll() {
        return services.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Director getById(@PathVariable("id") int directorId) {
        return services.getById(directorId);
    }

    @Override
    @PostMapping
    public Director add(@Valid @RequestBody Director newDirector) {
        return services.add(newDirector);
    }

    @Override
    @PutMapping
    public Director update(@Valid @RequestBody Director updatedDirector) {
        return services.update(updatedDirector);
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable("id") int directorId) {
        services.deleteDirector(directorId);
    }
}
