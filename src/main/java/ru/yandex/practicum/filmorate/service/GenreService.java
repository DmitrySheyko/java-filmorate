package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.List;

@Service
public class GenreService implements Services<Genre> {
    private final Storages<Genre> storages;

    @Autowired
    public GenreService(Storages<Genre> storages) {
        this.storages = storages;
    }

    public List<Genre> getAll() {
        return storages.getAll();
    }

    public Genre getById(int genreId) {
        return storages.getById(genreId);
    }

    @Override
    public Genre add(Genre newGenre) {
        return storages.add(newGenre);
    }

    @Override
    public Genre update(Genre updatedGenre) {
        return storages.update(updatedGenre);
    }
}
