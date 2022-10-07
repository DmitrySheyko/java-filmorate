package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Repository
@AllArgsConstructor
public class UserDbStorage implements Storages<User> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;
    private final RowMapper<Film> filmMapper;

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
        String sqlQuery = "SELECT user_id, " +
                "user_name, " +
                "login, email, " +
                "birth_day " +
                "FROM users " +
                "WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, userMapper, userId);
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

    public String addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO users_friends (user_id, friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return "Пользователь user_id=" + userId
                + " успешно добавлен в друзья пользователю user_id=" + friendId + ".";
    }

    @Override
    public User update(User updatedUser) {
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
    }

    public String deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM users_friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return "Пользователь user_id=" + userId
                + " успешно удален из друзей пользователя user_id=" + friendId + ".";
    }

    public List<User> getListOfFriends(int userId) {
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

    public String deleteUserById(Integer userId) {
        String sqlQuery = "DELETE FROM USERS WHERE user_id = ? ";
        jdbcTemplate.update(sqlQuery, userId);
        return "Пользователь user_id=" + userId + " успешно удален.";
    }

    public List<Film> getRecommendation(int userId) {
        String sqlQuery = "SELECT fl1.user_id " +
                "FROM films_likes AS fl1 " +
                "LEFT JOIN films_likes AS fl2 " +
                "ON fl1.user_id = fl2.user_id " +
                "WHERE fl1.film_id IN (SELECT film_id FROM films_likes WHERE user_id = ?) " +
                "AND fl1.user_id <> ? " +
                "GROUP BY fl1.user_id  " +
                "ORDER BY COUNT (fl1.film_id) DESC, COUNT (fl2.film_id)  DESC LIMIT 1 ";
        Integer optimalUser = jdbcTemplate.queryForObject(sqlQuery,
                (ResultSet resultSet, int rowNum) -> resultSet.getInt("user_id"), userId, userId);
        if (!(optimalUser == null)) {
            String sqlQuery2 = "SELECT * " +
                    "FROM films_likes AS fl LEFT JOIN films AS f " +
                    "ON fl.film_id = f.film_id " +
                    "WHERE fl.user_id = ? " +
                    "AND fl.film_id NOT IN (SELECT film_id FROM films_likes WHERE user_id = ?)";
            return jdbcTemplate.query(sqlQuery2, filmMapper, optimalUser, userId);
        } else {
            return Collections.emptyList();
        }
    }
}
