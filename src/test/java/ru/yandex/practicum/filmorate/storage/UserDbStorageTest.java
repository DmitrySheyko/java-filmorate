package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:testUserAndFilmData.sql")
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void shouldGetUserById() {
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getById(4));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 4));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "TestUserName4"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("login", "TestUserLogin4"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("email", "TestUserEmail4@ru.ru"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("birthday", "2014-10-14"));
    }

    @Test
    public void shouldAddUser() {
        User testUser = userDbStorage.add(User.builder().name("TestUserName").login("TestUserLogin")
                .email("TestUserEmail@ru.ru").birthday("2000-10-10").build());
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getById(testUser.getId()));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", testUser.getId()));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "TestUserName"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("login", "TestUserLogin"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("email", "TestUserEmail@ru.ru"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("birthday", "2000-10-10"));
        List<User> userList = userDbStorage.getAll();
        assertThatList(userList).hasSizeBetween(5, 5);
    }

    @Test
    public void shouldUpdateUser() {
        userDbStorage.update(User.builder().id(1).name("UpdatedName").login("UpdatedLogin")
                .email("UpdatedEmail1@ru.ru").birthday("2019-10-11").build());
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getById(1));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "UpdatedName"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("login", "UpdatedLogin"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("email", "UpdatedEmail1@ru.ru"));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("birthday", "2019-10-11"));
    }

    @Test
    public void shouldGetAllUsers() {
        List<User> userList = userDbStorage.getAll();
        assertThatList(userList).hasSizeBetween(4, 4);
    }

    @Test
    public void shouldGetListOfFriends() {
        List<User> userList = userDbStorage.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        User testFriend2 = userList.get(2);
        userDbStorage.addFriend(testUser.getId(), testFriend1.getId());
        userDbStorage.addFriend(testUser.getId(), testFriend2.getId());
        List<User> friends = userDbStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
        assertThat(friends.get(1)).isEqualTo(testFriend2);
    }

    @Test
    public void shouldAddFriend() {
        List<User> userList = userDbStorage.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userDbStorage.addFriend(testUser.getId(), testFriend1.getId());
        List<User> friends = userDbStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
    }

    @Test
    public void shouldDeleteFriend() {
        List<User> userList = userDbStorage.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userDbStorage.addFriend(testUser.getId(), testFriend1.getId());
        List<User> friends = userDbStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
        userDbStorage.deleteFriend(testUser.getId(), testFriend1.getId());
        List<User> friendsAfterDelete = userDbStorage.getListOfFriends(testUser.getId());
        assertThatList(friendsAfterDelete).hasSizeBetween(0, 0);
    }

    @Test
    public void shouldGetListOfCommonFriends() {
        List<User> userList = userDbStorage.getAll();
        User testUser1 = userList.get(0);
        User testUser2 = userList.get(1);
        User testFriend1 = userList.get(2);
        User testFriend2 = userList.get(3);
        userDbStorage.addFriend(testUser1.getId(), testFriend1.getId());
        userDbStorage.addFriend(testUser1.getId(), testFriend2.getId());
        userDbStorage.addFriend(testUser2.getId(), testFriend1.getId());
        userDbStorage.addFriend(testUser2.getId(), testFriend2.getId());
        List<User> commonFriends = userDbStorage.getListOfCommonFriends(testUser1.getId(), testUser2.getId());
        assertThat(commonFriends.get(0)).isEqualTo(testFriend1);
        assertThat(commonFriends.get(1)).isEqualTo(testFriend2);
    }

    @Test
    public void shouldGetListOfRecommendations() {
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(2, 2);
        filmDbStorage.addLike(3, 2);
        List<Film> result = userDbStorage.getRecommendation(1);
        assertThat(result.get(0).getId()).isEqualTo(2);
        result = userDbStorage.getRecommendation(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }
}