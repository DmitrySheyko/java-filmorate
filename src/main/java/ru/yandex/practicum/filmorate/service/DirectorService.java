package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService implements Services<Director> {
    private final DirectorDbStorage storages;

    @Override
    public List<Director> getAll() {
        return storages.getAll();
    }

    @Override
    public Director getById(int directorId) {
        return storages.getById(directorId);
    }

    @Override
    public Director add(Director newDirector) {
        validateDirector(newDirector);
        return storages.add(newDirector);
    }

    @Override
    public Director update(Director updatedDirector) {
        validateDirector(updatedDirector);
        return storages.update(updatedDirector);
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.info("Указано некорректное имя режиссера");
            throw new ValidationException(String.format("Указано некорректное имя режиссера id = :" + director.getId()));
        }
    }

    public void deleteDirector(int directorId) {
        storages.deleteDirector(directorId);
    }
}
