package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
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
        String sqlQuery = "SELECT rating_id, rating_name FROM ratings";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    public Mpa getMpaRatingById(int ratingId) {
        if (isMpaRatingInStorage(ratingId)) {
            String sqlQuery = "SELECT rating_id, rating_name FROM ratings WHERE rating_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, ratingId);
        } else {
            throw new ObjectNotFoundException(String.format("Рейтинг MPA id=%s не найден", ratingId));
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }

    private boolean isMpaRatingInStorage(Mpa mpa) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM ratings WHERE rating_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, mpa.getId());
    }

    private boolean isMpaRatingInStorage(int mpaId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 from ratings WHERE rating_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, mpaId);
    }
}