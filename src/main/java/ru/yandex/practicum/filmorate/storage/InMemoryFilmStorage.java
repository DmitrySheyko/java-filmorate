package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен новый фильм id={}", newFilm.getId());
        return newFilm;
    }

    public Film updateFilm(Film updatedFilm){
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Данные о фильме id = {} обновлены", updatedFilm.getId());
        return updatedFilm;
    }

    public boolean checkIsFilmInStorage(Film film) {
        return films.containsValue(film);
    }

    public boolean checkIsFilmInStorage(int filmId) {
        return films.containsKey(filmId);
    }
}
