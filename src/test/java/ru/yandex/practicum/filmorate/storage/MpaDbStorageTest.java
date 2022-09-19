package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void ShouldGetAllMpaRatings() {
        List<Mpa> listOfMpa = mpaDbStorage.getAll();
        assertThatList(listOfMpa).hasSizeBetween(5, 5);
        assertThat(listOfMpa.get(0)).isEqualTo(Mpa.builder().id(1).name("G").build());
    }

    @Test
    void ShouldGetMpaRatingById() {
        assertThat(mpaDbStorage.getById(2)).isEqualTo(Mpa.builder().id(2).name("PG")
                .build());
    }

    @Test
    void ShouldAddMpaRating() {
        Mpa testRating = mpaDbStorage.add(Mpa.builder().name("TestRating1").build());
        Optional<Mpa> ratingOptional = Optional.ofNullable(mpaDbStorage.getById(testRating.getId()));
        assertThat(ratingOptional).isPresent().hasValueSatisfying(rating ->
                assertThat(rating).hasFieldOrPropertyWithValue("name", testRating.getName()));
        List<Mpa> ratingList = mpaDbStorage.getAll();
        assertThatList(ratingList).hasSizeBetween(6, 6);
    }

    @Test
    void ShouldUpdateMpaRating(){
        Mpa testRating = mpaDbStorage.update(Mpa.builder().id(3).name("UpdatedRating").build());
        Optional<Mpa> ratingOptional = Optional.ofNullable(mpaDbStorage.getById(3));
        assertThat(ratingOptional).isPresent().hasValueSatisfying(rating ->
                assertThat(rating).hasFieldOrPropertyWithValue("name", testRating.getName()));

    }
}