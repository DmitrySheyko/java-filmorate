package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.swing.*;
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

    public List<Film> getRecommendation(int userId) {  // Дмимтрий add-recommendation
        if (checkIsObjectInStorage(userId)) {
            String sqlQuery = "WITH same_taste_users AS " +
                    " (SELECT user_id ," +
                    " COUNT (film_id) as likes_number " +
                    " FROM films_likes " +
                    " WHERE film_id IN (SELECT film_id FROM films_likes WHERE user_id = ?) " +
                    " AND user_id <> ?" +
                    " GROUP BY user_id) " +

                    " SELECT films_likes.user_id, likes_number, COUNT (films_likes.film_id) " +
                    " FROM  same_taste_users " +
                    " LEFT JOIN films_likes " +
                    " ON same_taste_users.user_id = films_likes.user_id " +
                    " WHERE likes_number = (select max(likes_number) from same_taste_users) " +
                    " GROUP BY films_likes.user_id " +
                    " ORDER BY COUNT (films_likes.film_id) DESC" +
                    " LIMIT 1";
            List<Integer> listOptimalUser = jdbcTemplate.query(sqlQuery, this::mapRowToUserId, userId, userId);
            if (!listOptimalUser.isEmpty()) {
                Integer optimalUser = jdbcTemplate.query(sqlQuery, this::mapRowToUserId, userId, userId).get(0);
                String sqlQuery2 =
                        " WITH result_film_id_genre AS  " +
                                " (SELECT film_id, " +
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
                return jdbcTemplate.query(sqlQuery2, filmMapper, listOptimalUser.get(0));
            } else {
                return null;
            }
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    //временно
    public List<String> filmsLikes() {
        String sqlQuery = "Select * from films_likes";
        List<String> map2 = new ArrayList<>();
        jdbcTemplate.query(sqlQuery, (ResultSetExtractor<List>) rs -> {
            String map = null;
            while (rs.next()) {
                map = (String.format(" User - %s: Film - %s ", rs.getString("user_id"), rs.getString("film_id")));
                map2.add(map);
            }
            return Collections.singletonList(map);
        });
        return map2;
    }

    private Integer mapRowToUserId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}
