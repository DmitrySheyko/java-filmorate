package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectorService implements Services<Director> {
    private final Storages<Director> storages;

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
        return storages.add(newDirector);
    }

    @Override
    public Director update(Director updatedDirector) {
        return storages.update(updatedDirector);
    }
}
