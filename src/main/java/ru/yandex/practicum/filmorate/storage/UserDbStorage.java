package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class UserDbStorage implements Storages<User> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT user_id, " +
                "user_name, " +
                "login, " +
                "email, " +
                "birth_day " +
                "FROM users " +
                "ORDER BY user_id";
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public User getById(int userId) {
        if (checkIsObjectInStorage(userId)) {
            String sqlQuery = "SELECT user_id, " +
                    "user_name, " +
                    "login, email, " +
                    "birth_day " +
                    "FROM users " +
                    "WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, userMapper, userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    @Override
    public User add(User newUser) {
        String sqlQuery = "INSERT INTO users (user_name, login, email, birth_day) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, newUser.getName());
            stmt.setString(2, newUser.getLogin());
            stmt.setString(3, newUser.getEmail());
            stmt.setString(4, newUser.getBirthday());
            return stmt;
        }, keyHolder);
        newUser.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return newUser;
    }

    public void addFriend(int userId, int friendId) {
        boolean isUserExist = checkIsObjectInStorage(userId);
        boolean isFriendExist = checkIsObjectInStorage(friendId);
        if (isUserExist && isFriendExist) {
            String sqlQuery = "INSERT INTO users_friends (user_id, friend_id) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.",
                    isUserExist ? friendId : userId));
        }
    }

    @Override
    public User update(User updatedUser) {
        if (checkIsObjectInStorage(updatedUser)) {
            String sqlQuery = "UPDATE users " +
                    "SET user_name = ?, login = ?, email = ?, birth_day = ? " +
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

    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM users_friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getListOfFriends(int userId) {
        if (checkIsObjectInStorage(userId)) {
            String sqlQuery = "SELECT u.user_id, " +
                    "u.user_name, " +
                    "u.login, " +
                    "u.email, " +
                    "u.birth_day " +
                    "FROM users_friends AS uf LEFT JOIN users AS u " +
                    "ON uf.friend_id = u.user_id " +
                    "WHERE uf.user_id = ?" +
                    "ORDER BY u.user_id";
            return jdbcTemplate.query(sqlQuery, userMapper, userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        String sqlQuery = "SELECT u.user_id, " +
                "u.user_name, " +
                "u.email, " +
                "u.login, " +
                "u.birth_day " +
                "FROM users_friends AS uf " +
                "LEFT JOIN users AS u ON uf.friend_id = u.user_id " +
                "WHERE uf.user_id = ? " +
                "AND uf.friend_id IN (SELECT friend_id FROM users_friends WHERE user_id = ?) ";
        return jdbcTemplate.query(sqlQuery, userMapper, userId, friendId);
    }

    @Override
    public boolean checkIsObjectInStorage(User user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user.getId()));
    }

    @Override
    public boolean checkIsObjectInStorage(int user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user));
    }
}
