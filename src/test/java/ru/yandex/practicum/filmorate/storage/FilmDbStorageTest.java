package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:testFilmData.sql")
class FilmDbStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Test
    void shouldGetAllFilms() {
        List<Film> filmsList = filmStorage.getAllFilms();
        assertThatList(filmsList).hasSizeBetween(4, 4);
    }

    @Test
    void shouldAddFilm() {
        Film testFilm = filmStorage.addFilm(Film.builder().name("TestFilmName").description("TestDescription")
                .releaseDate("2000-10-10").duration(100).mpa(Mpa.builder().id(1).build()).build());
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", testFilm.getName()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("description", testFilm.getDescription()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("duration", testFilm.getDuration()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate()));
        List<Film> filmsList = filmStorage.getAllFilms();
        assertThatList(filmsList).hasSizeBetween(5, 5);
    }

    @Test
    void shouldUpdateFilm() {
        List<Film> listOfFilms = filmStorage.getAllFilms();
        filmStorage.updateFilm(Film.builder().id(listOfFilms.get(0).getId()).name("UpdatedFilmName")
                .description("UpdatedDescription").releaseDate("2019-10-19").duration(200)
                .mpa(Mpa.builder().id(2).build()).build());
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(listOfFilms.get(0).getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", listOfFilms.get(0).getId()));
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
        List<User> listOfUsers = userStorage.getAllUsers();
        User testUser1 = listOfUsers.get(0);
        User testUser2 = listOfUsers.get(1);
        List<Film> listOfFilms = filmStorage.getAllFilms();
        Film testFilm = listOfFilms.get(3);
        filmStorage.addLike(testFilm.getId(), testUser1.getId());
        filmStorage.addLike(testFilm.getId(), testUser2.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(4);
        assertThat(popularFilms.get(0)).isEqualTo(testFilm);
    }

    @Test
    void shouldDeleteLike() {
        List<User> listOfUsers = userStorage.getAllUsers();
        User testUser1 = listOfUsers.get(0);
        User testUser2 = listOfUsers.get(1);
        List<Film> listOfFilms = filmStorage.getAllFilms();
        Film testFilm = listOfFilms.get(3);
        filmStorage.addLike(testFilm.getId(), testUser1.getId());
        filmStorage.addLike(testFilm.getId(), testUser2.getId());
        filmStorage.deleteLike(testFilm.getId(), testUser1.getId());
        filmStorage.deleteLike(testFilm.getId(), testUser2.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(4);
        assertThat(popularFilms.get(3)).isEqualTo(testFilm);
    }

    @Test
    void shouldGetFilmById() {
        Film testFilm = filmStorage.addFilm(Film.builder().name("TestFilmName").description("TestDescription")
                .releaseDate("2000-10-10").duration(100).mpa(Mpa.builder().id(1).build()).build());
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", testFilm.getId()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", testFilm.getName()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("description", testFilm.getDescription()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("duration", testFilm.getDuration()));
        assertThat(filmOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate()));
        List<Film> filmsList = filmStorage.getAllFilms();
        assertThatList(filmsList).hasSizeBetween(5, 5);
    }

    @Test
    void shouldGetPopularFilms() {
        List<User> listOfUsers = userStorage.getAllUsers();
        User testUser1 = listOfUsers.get(0);
        User testUser2 = listOfUsers.get(1);
        List<Film> listOfFilms = filmStorage.getAllFilms();
        System.out.println(listOfFilms.toString());
        Film testFilm1 = listOfFilms.get(3);  // 4
        Film testFilm2 = listOfFilms.get(2);  // 3
        Film testFilm3 = listOfFilms.get(0);  // 1
        filmStorage.addLike(testFilm1.getId(), testUser1.getId());
        filmStorage.addLike(testFilm1.getId(), testUser2.getId());
        filmStorage.addLike(testFilm2.getId(), testUser1.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(3);
        System.out.println(popularFilms.toString());
        assertThat(popularFilms.get(0)).isEqualTo(testFilm1);
        assertThat(popularFilms.get(1)).isEqualTo(testFilm2);
        assertThat(popularFilms.get(2)).isEqualTo(testFilm3);
        assertThatList(popularFilms).hasSizeBetween(3, 3);
    }
}