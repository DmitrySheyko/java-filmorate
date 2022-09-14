package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAllMpaRatings() {
        String sqlQuery = "select RATING_ID, RATING_NAME from RATINGS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    public Mpa getMpaRatingById(String ratingId) {
        String sqlQuery = "select RATING_ID, RATING_NAME from RATINGS where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, ratingId);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}