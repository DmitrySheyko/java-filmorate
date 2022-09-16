package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.duration," +
                "f.rating, " +
                "f.release_date, " +
                "f.rating, " +
                "r.rating_name, " +
                "result.genre_id_name " +
                "FROM films AS f LEFT JOIN ratings AS r " +
                "ON f.rating=r.rating_id " +
                "LEFT JOIN (SELECT fg.film_id, " +
                "STRING_AGG(genre_id_concat_name.genre_concat_name , ',') AS genre_id_name " +
                "FROM films_genres AS fg " +
                "LEFT JOIN (select genre_id, (genre_id || ':' || genre_name) AS genre_concat_name " +
                "FROM genres as g ) AS genre_Id_concat_name " +
                "ON fg.genre_id =  genre_Id_concat_name.genre_id " +
                "GROUP BY  fg.film_id) AS result " +
                "ON f.film_id = result.film_id " +
                "ORDER BY f.film_id ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film newFilm) {
        String sqlQuery = "INSERT INTO films (film_name, description, release_date, duration, rating ) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, newFilm.getName());
            stmt.setString(2, newFilm.getDescription());
            stmt.setString(3, newFilm.getReleaseDate());
            stmt.setInt(4, newFilm.getDuration());
            stmt.setInt(5, newFilm.getMpa().getId());
            return stmt;
        }, keyHolder);
        newFilm.setId(keyHolder.getKey().intValue());
        if (newFilm.getGenres() != null) {
            for (Genre genre : newFilm.getGenres()) {
                String sqlQueryForGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlQueryForGenres, newFilm.getId(), genre.getId());
            }
        }
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (checkIsFilmInStorage(updatedFilm)) {
            String sqlQuery = "UPDATE films SET " +
                    "film_name = ?, description = ?, release_date = ?, duration = ?, rating = ? " +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery
                    , updatedFilm.getName()
                    , updatedFilm.getDescription()
                    , updatedFilm.getReleaseDate()
                    , updatedFilm.getDuration()
                    , updatedFilm.getMpa().getId()
                    , updatedFilm.getId());
            if (updatedFilm.getGenres() != null) {
                Set<Genre> genreSet = new HashSet<>(updatedFilm.getGenres());
                updatedFilm.getGenres().clear();
                updatedFilm.getGenres().addAll(genreSet);
                updatedFilm.getGenres().sort(Comparator.comparingInt(Genre::getId));
                String sqlQueryForDeleteOldGenres = "delete from films_genres where film_id = ?";
                jdbcTemplate.update(sqlQueryForDeleteOldGenres, updatedFilm.getId());
                for (Genre genre : updatedFilm.getGenres()) {
                    String sqlQueryForAddGenres = "insert into films_genres (film_id, genre_id) values(?, ?)";
                    jdbcTemplate.update(sqlQueryForAddGenres, updatedFilm.getId(), genre.getId());
                }
            }
            return updatedFilm;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден.", updatedFilm.getId()));
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userid) {
        if (checkIsFilmHasLikeFromUser(filmId, userid)) {
            String sqlQuery = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlQuery, filmId, userid);
        } else {
            throw new ObjectNotFoundException(String.format("Лайк от пользователя id=%s фмльму id=%s не найден",
                    userid, filmId));
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        if (checkIsFilmInStorage(filmId)) {
            String sqlQuery = "SELECT f.film_id, " +
                    "f.film_name, " +
                    "f.description, " +
                    "f.duration," +
                    "f.rating, " +
                    "f.release_date, " +
                    "f.rating, " +
                    "r.rating_name, " +
                    "result.genre_id_name " +
                    "FROM films AS f LEFT JOIN ratings AS r " +
                    "ON f.rating=r.rating_id " +
                    "LEFT JOIN (SELECT fg.film_id, " +
                    "STRING_AGG(genre_id_concat_name.genre_concat_name , ',') AS genre_id_name " +
                    "FROM films_genres AS fg " +
                    "LEFT JOIN (SELECT genre_id, (genre_id || ':' || genre_name) AS genre_concat_name " +
                    "FROM genres AS g ) AS genre_Id_concat_name " +
                    "ON fg.genre_id =  genre_Id_concat_name.genre_id " +
                    "GROUP BY  fg.film_id) AS result " +
                    "ON f.film_id = result.film_id " +
                    "WHERE f.film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден", filmId));
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getString("release_date"))
                .duration(resultSet.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("rating"))
                        .name(resultSet.getString("rating_name"))
                        .build())
                .genres(createGenreListFromSting(resultSet.getString("genre_id_name")))
                .build();
    }

    private List<Genre> createGenreListFromSting(String rowStringGenres) {
        if (rowStringGenres != null) {
            String[] genreIdAndName = rowStringGenres.split(",");
            ArrayList<Genre> listOfGenres = new ArrayList<>(genreIdAndName.length);
            for (String idAndName : genreIdAndName) {
                String[] value = idAndName.split(":");
                listOfGenres.add(Genre.builder()
                        .id(Integer.parseInt(value[0]))
                        .name(value[1])
                        .build());
            }
            System.out.println("List of genres: " + listOfGenres);
            return listOfGenres;
        } else {
            return null;
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT film_full_info.film_id, " +
                "film_full_info.film_name, " +
                "film_full_info.description, " +
                "film_full_info.duration, " +
                "film_full_info.rating, " +
                "film_full_info.rating_name, " +
                "film_full_info.release_date, " +
                "film_full_info.genre_id_name, " +
                "count (fl.user_id) " +
                "FROM films_likes AS fl RIGHT JOIN (SELECT f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.duration, " +
                "f.release_date, " +
                "f.rating, " +
                "r.rating_name, " +
                "result.genre_id_name FROM films AS f LEFT JOIN ratings AS r " +
                "ON f.rating=r.rating_id LEFT JOIN (SELECT fg.film_id, " +
                "STRING_AGG(genre_id_concat_name.genre_concat_name , ',') AS genre_id_name " +
                "FROM films_genres AS fg LEFT JOIN (SELECT genre_id, (genre_id || ':' || genre_name) AS genre_concat_name " +
                "FROM genres AS g ) AS genre_Id_concat_name " +
                "ON fg.genre_id =  genre_Id_concat_name.genre_id " +
                "GROUP BY  fg.film_id) AS result " +
                "ON f.film_id = result.film_id) AS film_full_info " +
                "ON fl.film_id = film_full_info.film_id " +
                "GROUP BY film_full_info.film_id, " +
                "film_full_info.film_name, " +
                "film_full_info.description, " +
                "film_full_info.duration, " +
                "film_full_info.rating, " +
                "film_full_info.rating_name, " +
                "film_full_info.release_date,  " +
                "film_full_info.genre_id_name " +
                "ORDER BY count(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private boolean checkIsFilmInStorage(Film film) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, film.getId());
    }

    private boolean checkIsFilmInStorage(int filmId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId);
    }

    private boolean checkIsFilmHasLikeFromUser(Film film, User user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, film.getId(), user.getId());
    }

    private boolean checkIsFilmHasLikeFromUser(int filmId, int userId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId);
    }
}
