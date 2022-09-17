package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final static Integer REQUEST_TO_FRIENDS_STATUS = 1;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, user_name, login, email, birth_day FROM users ORDER BY user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(int userId) {
        if (checkIsUserInStorage(userId)) {
            String sqlQuery = "SELECT user_id, user_name, login, email, birth_day " +
                    "FROM users WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("user_name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getString("birth_day"))
                .build();
    }

    @Override
    public User addUser(User newUser) {
        String sqlQuery = "INSERT INTO users (user_name, login, email, birth_day) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, newUser.getName());
            stmt.setString(2, newUser.getLogin());
            stmt.setString(3, newUser.getEmail());
            stmt.setString(4, newUser.getBirthday());
            return stmt;
        }, keyHolder);
        newUser.setId(keyHolder.getKey().intValue());
        return newUser;
    }

    public void addFriend(int userId, int friendId) {
        if (checkIsUserInStorage(userId)) {
            if (checkIsUserInStorage(friendId)) {
                String sqlQuery = "INSERT INTO users_friends (user_id, friend_id, status) VALUES (?, ?, ?)";
                jdbcTemplate.update(sqlQuery, userId, friendId, REQUEST_TO_FRIENDS_STATUS);
            } else {
                throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", friendId));
            }
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        if (checkIsUserInStorage(updatedUser)) {
            String sqlQuery = "UPDATE users SET user_name = ?, login = ?, email = ?, birth_day = ? " +
                    "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery
                    , updatedUser.getName()
                    , updatedUser.getLogin()
                    , updatedUser.getEmail()
                    , updatedUser.getBirthday()
                    , updatedUser.getId());
            return updatedUser;
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", updatedUser.getId()));
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getListOfFriends(int userId) {
        if (checkIsUserInStorage(userId)) {
            String sqlQuery = "SELECT u.user_id, u.user_name, u.login, u.email, u.birth_day " +
                    "FROM users_friends AS uf LEFT JOIN users AS u " +
                    "ON uf.friend_id = u.user_id " +
                    "WHERE uf.user_id = ?" +
                    "ORDER BY u.user_id" ;
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        String sqlQuery = "WITH friends AS " +
                "( SELECT user_id, friend_id " +
                "FROM users_friends AS uf " +
                "WHERE uf.status IS NOT NULL ) " +
                "SELECT u.user_id, u.user_name, u.email, u.login, u.birth_day " +
                "FROM users u JOIN friends f1 " +
                "ON u.user_id = f1.friend_id " +
                "JOIN friends f2 " +
                "ON f1.friend_id = f2.friend_id " +
                "AND f1.friend_id <> f2.user_id " +
                "AND f2.friend_id <> f1.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, friendId);
    }

    public boolean checkIsUserInStorage(User user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user.getId());
    }

    public boolean checkIsUserInStorage(int user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user);
    }

    public void deleteUserById (int userId){
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }
}
