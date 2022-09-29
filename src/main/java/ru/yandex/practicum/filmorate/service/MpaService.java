package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.List;

@Service
public class MpaService implements Services<Mpa> {
    private final Storages<Mpa> storages;

    @Autowired
    public MpaService(Storages<Mpa> storages) {
        this.storages = storages;
    }

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
