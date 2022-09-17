package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getAllMpaRatings() {
        List<Mpa> listOfMpa = mpaDbStorage.getAllMpaRatings();
        assertThatList(listOfMpa).hasSizeBetween(5, 5);
        assertThat(listOfMpa.get(0)).isEqualTo(Mpa.builder().id(1).name("G").build());
    }

    @Test
    void getMpaRatingById() {
        assertThat(mpaDbStorage.getMpaRatingById(2)).isEqualTo(Mpa.builder().id(2).name("PG").build());
    }
}