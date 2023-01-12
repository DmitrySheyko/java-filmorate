package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaService implements Services<Mpa> {
    private final Storages<Mpa> mpaDbStorage;

    public List<Mpa> getAll() {
        try {
            List<Mpa> ratings = mpaDbStorage.getAll();
            log.info("Получен список жанров");
            return ratings;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public Mpa getById(int ratingId) {
        try {
            Mpa rating = mpaDbStorage.getById(ratingId);
            log.info("Получен рейтинг rating_id=" + ratingId + ".");
            return rating;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Рейтинг rating_id=" + ratingId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    @Override
    public Mpa add(Mpa newRating) {
        mpaDbStorage.add(newRating);
        log.info("Рейтинг rating_id=" + newRating + " успешно добавлен.");
        return newRating;
    }

    @Override
    public Mpa update(Mpa mpaForUpdate) {
        try {
            mpaDbStorage.update(mpaForUpdate);
            log.info("Рейтинг rating_id=" + mpaForUpdate.getId() + " успешно обновлен.");
            return mpaForUpdate;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Рейтинг rating_id=" + mpaForUpdate.getId() + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }
}
