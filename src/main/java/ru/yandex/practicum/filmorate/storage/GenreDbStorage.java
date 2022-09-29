package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class GenreDbStorage implements Storages<Genre> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreMapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT genre_id, " +
                "genre_name " +
                "FROM genres " +
                "ORDER BY genre_id ";
        return jdbcTemplate.query(sqlQuery, genreMapper);
    }

    @Override
    public Genre getById(int genreId) {
        if (checkIsObjectInStorage(genreId)) {
            String sqlQuery = "SELECT genre_id, " +
                    "genre_name " +
                    "FROM genres WHERE genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, genreMapper, genreId);
        } else {
            throw new ObjectNotFoundException(String.format("Жанр id=%s не найден", genreId));
        }
    }

    @Override
    public Genre add(Genre newGenre) {
        String sqlQuery = "INSERT INTO genres (genre_name) VALUES(?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"genre_id"});
            stmt.setString(1, newGenre.getName());
            return stmt;
        }, keyHolder);
        newGenre.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return newGenre;
    }

    @Override
    public Genre update(Genre updatedGenre) {
        String sqlQuery = "UPDATE genres SET genre_name = ? " +
                "WHERE genre_id = ? ";
        jdbcTemplate.update(sqlQuery, updatedGenre.getName(), updatedGenre.getId());
        return updatedGenre;
    }

    @Override
    public boolean checkIsObjectInStorage(int genreId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM genres WHERE genre_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, genreId));
    }

    @Override
    public boolean checkIsObjectInStorage(Genre genre) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM genres WHERE genre_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, genre.getId()));
    }
}