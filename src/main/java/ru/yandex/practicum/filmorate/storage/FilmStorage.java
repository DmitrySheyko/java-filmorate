package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film addFilm(Film newFilm);

    Film updateFilm(Film updatedFilm);

    void addLike (int filmId, int userId);

    void deleteLike(int filmId, int userid);

    Film getFilmById(int filmId);

    List<Film> getPopularFilms(int count);
}
