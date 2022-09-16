package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

        @Test
        public void testFindUserById() {

            Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

            assertThat(userOptional)
                    .isPresent()
                    .hasValueSatisfying(user ->
                            assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                    );
        }
    }

}