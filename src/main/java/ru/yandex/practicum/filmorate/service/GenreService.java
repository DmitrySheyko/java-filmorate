package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService implements Services<Genre> {
    private final Storages<Genre> genreDbStorage;

    public List<Genre> getAll() {
        try {
            List<Genre> genres = genreDbStorage.getAll();
            log.info("Получен список жанров");
            return genres;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public Genre getById(int genreId) {
        try {
            Genre genre = genreDbStorage.getById(genreId);
            log.info("Получен жанр genre_id=" + genreId + ".");
            return genre;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Жанр genre_id=" + genreId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    @Override
    public Genre add(Genre newGenre) {
        genreDbStorage.add(newGenre);
        log.info("Жанр genre_id=" + newGenre + " успешно добавлен.");
        return newGenre;
    }

    @Override
    public Genre update(Genre genreForUpdate) {
        try {
            genreDbStorage.update(genreForUpdate);
            log.info("Жанр genre_id=" + genreForUpdate.getId() + " успешно обновлен.");
            return genreForUpdate;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Жанр genre_id=" + genreForUpdate.getId() + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }
}
