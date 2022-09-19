package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAllGenres() {
        List<Genre> listOfGenres = genreDbStorage.getAll();
        assertThatList(listOfGenres).hasSizeBetween(6, 6);
        assertThat(listOfGenres.get(0)).isEqualTo(Genre.builder().id(1).name("Комедия").build());
    }

    @Test
    void getGenreById() {
        assertThat(genreDbStorage.getById(2)).isEqualTo(Genre.builder().id(2).name("Драма").build());
    }

    @Test
    void ShouldAddGenre() {
        Genre testGenre = genreDbStorage.add(Genre.builder().name("TestGenre1").build());
        Optional<Genre> genreOptional = Optional.ofNullable(genreDbStorage.getById(testGenre.getId()));
        assertThat(genreOptional).isPresent().hasValueSatisfying(genre ->
                assertThat(genre).hasFieldOrPropertyWithValue("name", testGenre.getName()));
        List<Genre> genreList = genreDbStorage.getAll();
        assertThatList(genreList).hasSizeBetween(7, 7);
    }

    @Test
    void ShouldUpdateGenre(){
        Genre testGenre = genreDbStorage.update(Genre.builder().id(1).name("UpdatedGenre").build());
        Optional<Genre> genreOptional = Optional.ofNullable(genreDbStorage.getById(1));
        assertThat(genreOptional).isPresent().hasValueSatisfying(genre ->
                assertThat(genre).hasFieldOrPropertyWithValue("name", testGenre.getName()));

    }
}