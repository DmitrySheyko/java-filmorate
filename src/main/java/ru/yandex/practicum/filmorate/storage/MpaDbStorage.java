package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class MpaDbStorage implements Storages<Mpa> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mpaMapper;

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT rating_id, " +
                "rating_name " +
                "FROM ratings " +
                "ORDER BY rating_id ";
        return jdbcTemplate.query(sqlQuery, mpaMapper);
    }

    @Override
    public Mpa getById(int ratingId) {
            String sqlQuery = "SELECT rating_id, " +
                    "rating_name " +
                    "FROM ratings " +
                    "WHERE rating_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, mpaMapper, ratingId);
    }

    @Override
    public Mpa add(Mpa newMpa) {
        String sqlQuery = "INSERT INTO ratings (rating_name) VALUES(?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"rating_id"});
            stmt.setString(1, newMpa.getName());
            return stmt;
        }, keyHolder);
        newMpa.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return newMpa;
    }

    @Override
    public Mpa update(Mpa mpaForUpdate) {
        String sqlQuery = "UPDATE ratings SET rating_name = ? " +
                "WHERE rating_id = ? ";
        jdbcTemplate.update(sqlQuery, mpaForUpdate.getName(), mpaForUpdate.getId());
        return mpaForUpdate;
    }

    public boolean checkIsObjectInStorage(Mpa mpa) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM ratings WHERE rating_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, mpa.getId()));
    }

    public boolean checkIsObjectInStorage(int mpaId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 from ratings WHERE rating_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, mpaId));
    }
}