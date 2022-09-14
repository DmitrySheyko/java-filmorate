package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
        String sqlQuery = "select USER_ID, USER_NAME, LOGIN, EMAIL, BIRTH_DAY from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(int userId) {
        if (checkIsUserInStorage(userId)) {
            String sqlQuery = "select USER_ID, user_name, login, email, birth_day " +
                    "from USERS where USER_ID = ?";
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
        String sqlQuery = "insert into USERS (USER_NAME, LOGIN, EMAIL, BIRTH_DAY) values (?, ?, ?, ?)";
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
                String sqlQuery = "insert into USERS_FRIENDS (USER_ID, FRIEND_ID, STATUS) values (?, ?, ?)";
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
            String sqlQuery = "update USERS set " +
                    "USER_NAME = ?, LOGIN = ?, EMAIL = ?, BIRTH_DAY = ? " +
                    "where USER_ID = ?";
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
        String sqlQuery = "delete from USERS_FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getListOfFriends(int userId) {
        if (checkIsUserInStorage(userId)) {
            String sqlQuery = "select U.USER_ID, U.USER_NAME, U.LOGIN, U.EMAIL, U.BIRTH_DAY " +
                    "from USERS_FRIENDS as UF left join USERS as U " +
                    "on UF.FRIEND_ID = U.USER_ID " +
                    "where UF.USER_ID = ?" +
                    "group by U.USER_ID";
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%s не найден.", userId));
        }
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        String sqlQuery = "WITH FRIENDS AS " +
                "( SELECT USER_ID, FRIEND_ID " +
                "FROM USERS_FRIENDS AS UF " +
                "WHERE UF.STATUS IS NOT NULL ) " +
                "SELECT U.USER_ID, U.USER_NAME, U.EMAIL, U.LOGIN, U.BIRTH_DAY " +
                "FROM USERS U JOIN FRIENDS F1 " +
                "ON U.USER_ID = F1.FRIEND_ID " +
                "JOIN FRIENDS F2 " +
                "ON F1.FRIEND_ID = F2.FRIEND_ID " +
                "AND F1.FRIEND_ID <> F2.USER_ID " +
                "AND F2.FRIEND_ID <> F1.USER_ID " +
                "WHERE F1.USER_ID = ? AND F2.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, friendId);
    }

    public boolean checkIsUserInStorage(User user) {
        String sqlQuery = "select exists (select 1 from users where user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user.getId());
    }

    public boolean checkIsUserInStorage(int user) {
        String sqlQuery = "select exists (select 1 from users where user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, user);
    }

}
