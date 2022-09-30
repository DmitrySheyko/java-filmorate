package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService implements Services<Genre> {
    private final Storages<Genre> storages;

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
