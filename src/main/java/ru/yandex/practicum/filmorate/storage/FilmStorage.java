package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film addFilm(Film newFilm);

    boolean checkIsFilmInStorage(Film film);

    boolean checkIsFilmInStorage(int filmId);
}
