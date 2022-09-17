package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAllGenres() {
        List<Genre> listOfGenres = genreDbStorage.getAllGenres();
        assertThatList(listOfGenres).hasSizeBetween(6, 6);
        assertThat(listOfGenres.get(0)).isEqualTo(Genre.builder().id(1).name("Комедия").build());
    }

    @Test
    void getGenreById() {
        assertThat(genreDbStorage.getGenreById(2)).isEqualTo(Genre.builder().id(2).name("Драма").build());
    }
}