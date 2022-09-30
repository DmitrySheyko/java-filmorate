package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService implements Services<Mpa> {
    private final Storages<Mpa> storages;

    public List<Mpa> getAll() {
        return storages.getAll();
    }

    public Mpa getById(int ratingId) {
        return storages.getById(ratingId);
    }

    @Override
    public Mpa add(Mpa newMpa) {
        return storages.add(newMpa);
    }

    @Override
    public Mpa update(Mpa updatedMpa) {
        return storages.update(updatedMpa);
    }
}
