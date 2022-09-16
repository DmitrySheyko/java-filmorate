package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre getGenreById(int genreId) {
        if (isGenreInStorage(genreId)) {
            String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
            System.out.println("Смотрим как работает");
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } else {
            throw new ObjectNotFoundException(String.format("Жанр id=%s не найден", genreId));
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    private boolean isGenreInStorage(int genreId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM genres WHERE genre_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, genreId);
    }

    private boolean isGenreInStorage(Genre genre) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM genres WHERE genre_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, genre.getId());
    }
}