package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
        log.info("Получен запрос на получение списка режиссеров");
        return storages.getAll();
    }

    @Override
    public Director getById(int directorId) {
        log.info("Получен запрос на получение режиссера id={}", directorId);
        try {
            Director director = storages.getById(directorId);
            return director;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Режиссер id=%s не найден", directorId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Режиссер id=%s не найден",
                    directorId));
        }
    }

    @Override
    public Director add(Director newDirector) {
        log.info("Получен запрос на создание режиссера name={}", newDirector.getName());
        validateDirector(newDirector);
        return storages.add(newDirector);
    }

    @Override
    public Director update(Director updatedDirector) {
        log.info("Получен запрос на обновление данных режиссера id={}", updatedDirector.getId());
        validateDirector(updatedDirector);
        try {
            Director director = storages.update(updatedDirector);
            return director;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Режиссер id=%s не найден", updatedDirector.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Режиссер id=%s не найден",
                    updatedDirector.getId()));
        }
    }

    private void validateDirector(Director director) {

        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.error("Указано некорректное имя режиссера");
            throw new ValidationException(String.format("Указано некорректное имя режиссера id = :" +
                    director.getId()));
        }
    }

    public void deleteDirector(int directorId) {
        log.info("Получен запрос на удаление режиссера с id={}", directorId);
        int rows = storages.deleteDirector(directorId);
        if (rows == 1) {
            log.info("Удален режиссер с id = {}. Всего удалено строк: {}", directorId, rows);
        } else {
            log.error("Что то пошло не так. Удалено строк: {}", rows);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Режиссер с id=%s не найдено.",
                    directorId));
        }
    }
}
