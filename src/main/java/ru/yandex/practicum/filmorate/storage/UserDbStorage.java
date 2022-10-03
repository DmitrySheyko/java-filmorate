package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // Дмимтрий add-recommendation
    public List<Film> getRecommendation(int userId) {
        if (checkIsObjectInStorage(userId)) {
            String sqlQuery = "WITH same_taste_users AS " +  // данным запросом вычисляем
                    " (SELECT user_id ," +                   // оптимального user для рекоммендаций
                    " COUNT (film_id) as likes_number " +
                    " FROM films_likes " +
                    " WHERE film_id IN (SELECT film_id FROM films_likes WHERE user_id = ?) " +
                    " AND user_id <> ?" +
                    " GROUP BY user_id) " +

                    " SELECT fl.user_id, " +
                    " likes_number, " +
                    " COUNT (fl.film_id) " +
                    " FROM  same_taste_users " +
                    " LEFT JOIN films_likes AS fl" +
                    " ON same_taste_users.user_id = fl.user_id " +
                    " WHERE likes_number = (select max(likes_number) from same_taste_users) " +
                    " GROUP BY fl.user_id " +
                    " ORDER BY COUNT (fl.film_id) DESC" +
                    " LIMIT 1";

            Integer optimalUser = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUserId, userId, userId);
            if (! (optimalUser == null)) {
                String sqlQuery2 = " WITH result_film_id_genre AS " +   // данным запросом получаем
                        " (SELECT film_id, " +                          // рекоммендованные фильмы
                        " group_concat (result_genre.id_concat_name) AS genre_id_name " +
                        " FROM films_genres AS fg " +
                        " LEFT JOIN (SELECT genre_id, (genre_id || ':' || genre_name) AS id_concat_name " +
                        " FROM genres AS g) AS result_genre " +
                        " ON fg.genre_id = result_genre.genre_id " +
                        " GROUP BY fg.film_id) " +

                        " SELECT f.film_id, " +
                        " f.film_name, " +
                        " f.description, " +
                        " f.duration, " +
                        " f.rating, " +
                        " f.release_date, " +
                        " f.rating, " +
                        " r.rating_name, " +
                        " result_film_Id_genre.genre_id_name " +
                        " FROM films AS f " +
                        " LEFT JOIN ratings AS r " +
                        " ON f.rating = r.rating_id " +
                        " LEFT JOIN result_film_id_genre " +
                        " ON f.film_id = result_film_Id_genre.film_id " +
                        " WHERE  f.film_id = ? " +
                        " ORDER BY f.film_id ";
                return jdbcTemplate.query(sqlQuery2, filmMapper, optimalUser);
            } else {
                return null;
            }
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    private Integer mapRowToUserId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}
