package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@AllArgsConstructor
public class DirectorDbStorage implements Storages<Director> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorMapper;

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "ORDER BY director_id ";
        return jdbcTemplate.query(sqlQuery, directorMapper);
    }

    @Override
    public Director getById(int directorId) {
        String sqlQuery = "SELECT * " +
                "FROM directors WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, directorMapper, directorId);
    }

    @Override
    public Director add(Director newDirector) {
        String sqlQuery = "INSERT INTO directors (director_name) VALUES(?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, newDirector.getName());
            return stmt;
        }, keyHolder);
        newDirector.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return newDirector;
    }

    @Override
    public Director update(Director updatedDirector) {
        getById(updatedDirector.getId());
        String sqlQuery = "UPDATE directors SET director_name = ? " +
                "WHERE director_id = ? ";
        jdbcTemplate.update(sqlQuery, updatedDirector.getName(), updatedDirector.getId());
        return updatedDirector;
    }

    public Set<Director> getDirectorsByFilmId(int filmId) {
        String directorRows = "SELECT * FROM directors " +
                "INNER JOIN films_directors FD " +
                "ON directors.director_id = FD.director_id " +
                "WHERE FD.film_id  = ? ";
        return new HashSet<>(jdbcTemplate.query(directorRows, directorMapper, filmId));
    }

    public int deleteDirector(int directorId) {
        String deleteSql = "DELETE FROM directors WHERE director_id = ?";
        return jdbcTemplate.update(deleteSql, directorId);
    }

    @Override
    public boolean checkIsObjectInStorage(Director director) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM directors WHERE director_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, director.getId()));
    }

    @Override
    public boolean checkIsObjectInStorage(int directorId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM directors WHERE director_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, directorId));
    }

}
