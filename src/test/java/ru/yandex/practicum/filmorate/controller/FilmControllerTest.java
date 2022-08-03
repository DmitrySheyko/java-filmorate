package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    static FilmController filmController;
    static Film testFilm;

    @BeforeAll
    static void beforeAll() {
        filmController = new FilmController();
    }

    @BeforeEach
    void beforeEach() {
        testFilm = new Film();
        testFilm.setId(100);
        testFilm.setName("TestFilm");
        testFilm.setDescription("Test description");
        testFilm.setReleaseDate("2010-10-10");
        testFilm.setDuration(2);
    }

    @Test
    void shouldApproveFilmWithCorrectData() throws ValidationException {
        assertTrue(filmController.checkIsFilmDataCorrect(testFilm),
                "Корректная версия Film не прошла проверку");
    }

    @Test
    void shouldDeclineFilmWithIncorrectName() {
        testFilm.setName(null);
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
        testFilm.setName("");
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
        testFilm.setName(" ");
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
    }

    @Test
    void shouldDeclineFilmWithIncorrectDescription() {
        testFilm.setDescription(null);
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
        testFilm.setName("111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111");
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
    }

    @Test
    void shouldDeclineFilmWithIncorrectReleaseDate() {
        testFilm.setReleaseDate("1895-12-27");
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
    }

    @Test
    void shouldDeclineFilmWithIncorrectDuration() {
        testFilm.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
        testFilm.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.checkIsFilmDataCorrect(testFilm));
    }
}