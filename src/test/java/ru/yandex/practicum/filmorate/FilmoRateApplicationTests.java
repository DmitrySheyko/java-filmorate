//package ru.yandex.practicum.filmorate;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.UserDbStorage;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class FilmoRateApplicationTests {
//    private final UserDbStorage userStorage;
//
//    @BeforeEach
//    public void beforeEach (){
//
//    }
//
//    @Test
//    public void testGetUserById() {
//        userStorage.addUser(User.builder()
//                .name("TestUserName1")
//                .login("TestUserLogin1")
//                .email("TestUserEmail@ru.ru")
//                .birthday("2010-10-10")
//                .build());
//        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
//        assertThat(userOptional)
//                .isPresent()
//                .hasValueSatisfying(user ->
//                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
//                );
//    }
//
//    @Test
//    public void addUser() {
//        userStorage.addUser(User.builder()
//                .name("TestUserName1")
//                .login("TestUserLogin1")
//                .email("TestUserEmail@ru.ru")
//                .birthday("2010-10-10")
//                .build());
//        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
//        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
//                assertThat(user).hasFieldOrPropertyWithValue("id", 1));
//        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
//                assertThat(user).hasFieldOrPropertyWithValue("name", "TestUserName1"));
//        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
//                assertThat(user).hasFieldOrPropertyWithValue("login", "TestUserLogin1"));
//        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
//                assertThat(user).hasFieldOrPropertyWithValue("email", "TestUserEmail@ru.ru"));
//        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
//                assertThat(user).hasFieldOrPropertyWithValue("birthday", "2010-10-10"));
//    }
//
//
//}