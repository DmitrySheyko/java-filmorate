package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:testUserData.sql")
class UserDbStorageTest {
    private final UserStorage userStorage;

    @Test
    public void shouldGetUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(4));
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
        User testUser = userStorage.addUser(User.builder().name("TestUserName").login("TestUserLogin")
                .email("TestUserEmail@ru.ru").birthday("2000-10-10").build());
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(testUser.getId()));
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
        List<User> userList = userStorage.getAllUsers();
        assertThatList(userList).hasSizeBetween(5, 5);
    }

    @Test
    public void shouldUpdateUser() {
        userStorage.updateUser(User.builder().id(1).name("UpdatedName").login("UpdatedLogin")
                .email("UpdatedEmail1@ru.ru").birthday("2019-10-11").build());
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
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
        List<User> userList = userStorage.getAllUsers();
        assertThatList(userList).hasSizeBetween(4, 4);
    }

    @Test
    public void shouldGetListOfFriends() {
        List<User> userList = userStorage.getAllUsers();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        User testFriend2 = userList.get(2);
        userStorage.addFriend(testUser.getId(), testFriend1.getId());
        userStorage.addFriend(testUser.getId(), testFriend2.getId());
        List<User> friends = userStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
        assertThat(friends.get(1)).isEqualTo(testFriend2);
    }

    @Test
    public void shouldAddFriend() {
        List<User> userList = userStorage.getAllUsers();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userStorage.addFriend(testUser.getId(), testFriend1.getId());
        List<User> friends = userStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
    }

    @Test
    public void shouldDeleteFriend() {
        List<User> userList = userStorage.getAllUsers();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userStorage.addFriend(testUser.getId(), testFriend1.getId());
        List<User> friends = userStorage.getListOfFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
        userStorage.deleteFriend(testUser.getId(), testFriend1.getId());
        List<User> friendsAfterDelete = userStorage.getListOfFriends(testUser.getId());
        assertThatList(friendsAfterDelete).hasSizeBetween(0, 0);
    }

    @Test
    public void shouldGetListOfCommonFriends() {
        List<User> userList = userStorage.getAllUsers();
        User testUser1 = userList.get(0);
        User testUser2 = userList.get(1);
        User testFriend1 = userList.get(2);
        User testFriend2 = userList.get(3);
        userStorage.addFriend(testUser1.getId(), testFriend1.getId());
        userStorage.addFriend(testUser1.getId(), testFriend2.getId());
        userStorage.addFriend(testUser2.getId(), testFriend1.getId());
        userStorage.addFriend(testUser2.getId(), testFriend2.getId());
        List<User> commonFriends = userStorage.getListOfCommonFriends(testUser1.getId(), testUser2.getId());
        assertThat(commonFriends.get(0)).isEqualTo(testFriend1);
        assertThat(commonFriends.get(1)).isEqualTo(testFriend2);
    }
}