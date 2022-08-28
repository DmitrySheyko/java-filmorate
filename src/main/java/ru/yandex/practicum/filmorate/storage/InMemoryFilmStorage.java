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

    public Film addFilm(Film newFilm) {
        newFilm.generateAndSetId();
        newFilm.generateSetOfLikes();
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен новый фильм id={}", newFilm.getId());
        return newFilm;
    }

    public Film updateFilm(Film updatedFilm){
        // Добавляем обновленной версии фильма список лайков от старой версии фильма
        updatedFilm.setSetOfLikes(films.get(updatedFilm.getId()).getSetOfLikes());
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Данные о фильме id = {} обновлены", updatedFilm.getId());
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        log.info("Наплавоен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    public Film getFilmById(int filmId){
        log.info("Наплавоен фильм id={}", filmId);
        return films.get(filmId);
    }

    public void addLike(int filmId, int userId){
        films.get(filmId).addLike(userId);
        log.info("Фильму id = {} добавлен лайк пользователя id={}", filmId, userId);
    }

    public void deleteLike(int filmId, int userId){
        films.get(filmId).deleteLike(userId);
        log.info("Для фильма id = {} удален лайк пользователя id={}", filmId, userId);
    }

    public boolean checkIsFilmInStorage(Film film) {
        return films.containsValue(film);
    }

    public boolean checkIsFilmInStorage(int filmId) {
        return films.containsKey(filmId);
    }

    public boolean checkIsFilmHasLikeFromUser(int filmId, int userId){
        return films.get(filmId).getSetOfLikes().contains(userId);
    }
}
