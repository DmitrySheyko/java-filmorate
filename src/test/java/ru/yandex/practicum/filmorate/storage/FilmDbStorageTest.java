package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:testUserAndFilmData.sql")
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void shouldGetAllFilms() {
        List<Film> filmsList = filmDbStorage.getAll();
        assertThatList(filmsList).hasSizeBetween(4, 4);
    }

    @Test
    void shouldAddFilm() {
        Film testFilm = filmDbStorage.add(Film.builder().name("TestFilmName").description("TestDescription")
                .releaseDate("2000-10-10").duration(100).mpa(Mpa.builder().id(1).build()).build());
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilmName"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("description", "TestDescription"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("duration", 100));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("releaseDate", "2000-10-10"));
        List<Film> filmsList = filmDbStorage.getAll();
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("mpa", Mpa.builder().id(1).name("G").build()));
        assertThatList(filmsList).hasSizeBetween(5, 5);
    }

    @Test
    void shouldUpdateFilm() {
        filmDbStorage.update(Film.builder().id(1).name("UpdatedFilmName")
                .description("UpdatedDescription").releaseDate("2019-10-19").duration(200)
                .mpa(Mpa.builder().id(2).build()).build());
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(1));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "UpdatedFilmName"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("description", "UpdatedDescription"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("duration", 200));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("releaseDate", "2019-10-19"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("mpa", Mpa.builder().id(2).name("PG").build()));
    }

    @Test
    void shouldAddLike() {
        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(3, 2);
        List<Film> popularFilms = filmDbStorage.getPopularFilms(4);
        assertThat(popularFilms.get(0)).isEqualTo(filmDbStorage.getById(3));
    }

    @Test
    void shouldDeleteLike() {
        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(3, 2);
        filmDbStorage.deleteLike(3, 1);
        filmDbStorage.deleteLike(3, 2);
        List<Film> popularFilms = filmDbStorage.getPopularFilms(4);
        assertThat(popularFilms.get(2)).isEqualTo(filmDbStorage.getById(3));
    }

    @Test
    void shouldGetFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(2));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 2));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilmName2"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("description", "TestFilmDescription2"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("duration", 20));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("releaseDate", "2012-10-12"));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("mpa", Mpa.builder().id(2).name("PG").build()));
    }

    @Test
    void shouldGetPopularFilms() {
        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(3, 2);
        filmDbStorage.addLike(3, 3);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        List<Film> popularFilms = filmDbStorage.getPopularFilms(3);
        System.out.println(popularFilms.toString());
        assertThat(popularFilms.get(0)).isEqualTo(filmDbStorage.getById(3));
        assertThat(popularFilms.get(1)).isEqualTo(filmDbStorage.getById(1));
        assertThat(popularFilms.get(2)).isEqualTo(filmDbStorage.getById(2));
        assertThatList(popularFilms).hasSizeBetween(3, 3);
    }
}