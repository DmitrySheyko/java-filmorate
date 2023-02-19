package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

/**
 * Class of controller for entity {@link Director}.
 *
 * @author DmitrySheyko
 */
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController implements Controllers<Director> {

    private final DirectorService service;

    @Override
    @GetMapping
    public List<Director> getAll() {
        return service.getAll();
    }

    @Override
    @GetMapping("{id}")
    public Director getById(@PathVariable("id") int directorId) {
        return service.getById(directorId);
    }

    @Override
    @PostMapping
    public Director add(@Valid @RequestBody Director newDirector) {
        return service.add(newDirector);
    }

    @Override
    @PutMapping
    public Director update(@Valid @RequestBody Director updatedDirector) {
        return service.update(updatedDirector);
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable("id") int directorId) {
        service.deleteDirector(directorId);
    }

}
