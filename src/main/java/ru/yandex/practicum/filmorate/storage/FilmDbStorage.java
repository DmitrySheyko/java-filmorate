package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.*;

@Repository("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements Storages<Film> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper;
    private final DirectorDbStorage directorDbStorage;

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, filmMapper);
    }

    @Override
    public Film add(Film newFilm) {
        String sqlQuery = "INSERT INTO films (film_name, description, release_date, duration, rating) " +
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
        newFilm.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (newFilm.getGenres() != null) {
            for (Genre genre : newFilm.getGenres()) {
                String sqlQueryForGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlQueryForGenres, newFilm.getId(), genre.getId());
            }
        }
        updateDirectorsOfFilm(newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film updatedFilm) {
        if (checkIsObjectInStorage(updatedFilm)) {
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
            updateDirectorsOfFilm(updatedFilm);
            return updatedFilm;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден.", updatedFilm.getId()));
        }
    }

    private void updateDirectorsOfFilm(Film film) {
        String sqlQueryForDeleteOldGenres = "delete from films_directors where film_id = ?";
        jdbcTemplate.update(sqlQueryForDeleteOldGenres, film.getId());
        if (!CollectionUtils.isEmpty(film.getDirectors())) {
            for (Director director : film.getDirectors()) {
                String sqlQueryForAddGenres = "insert into films_directors (film_id, director_id) values(?, ?)";
                jdbcTemplate.update(sqlQueryForAddGenres, film.getId(), director.getId());
            }
        }
    }

    public String addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return "Пользователь user_id=" + userId + " успешно поставил лайк фильму film_id=" + filmId + ".";
    }

    public String deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return "Пользователь user_id=" + userId + " успешно удалил лайк фильму film_id=" + filmId + ".";
    }

    @Override
    public Film getById(int filmId) {
        String sqlQuery = "SELECT * FROM films " +
                "WHERE films.film_id = ? ";
        return jdbcTemplate.queryForObject(sqlQuery, filmMapper, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.* FROM films f " +
                "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                "GROUP BY  f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, count);
    }

    public List<Film> getPopularFilmSortedByYear(int count, int year) {
        String sqlQuery = "SELECT f.* FROM films f " +
                "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                "WHERE year(f.release_date) = ? " +
                "GROUP BY  f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, filmMapper, year, count);
    }

    public List<Film> getPopularFilmSortedByGenre(int count, int genreId) {
        String sqlQuery = "SELECT f.* FROM films f " +
                "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                "LEFT JOIN films_genres fg ON F.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY  f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, genreId, count);
    }

    public List<Film> getPopularFilmSortedByGenreAndYear(int count, int genreId, int year) {
        String sqlQuery = "SELECT f.* FROM films f " +
                "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                "LEFT JOIN films_genres fg ON F.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? AND year(f.release_date) = ? " +
                "GROUP BY  f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, genreId, year, count);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (directorDbStorage.checkIsObjectInStorage(directorId)) {
            String selectQuery;
            if (sortBy.equals("year")) {
                selectQuery = "SELECT * FROM films f " +
                        "INNER JOIN films_directors fd " +
                        "ON fd.film_id = f.film_id " +
                        "WHERE fd.director_id = ? " +
                        "ORDER BY release_date";
            } else if (sortBy.equals("likes")) {
                selectQuery = "SELECT * FROM films f " +
                        "INNER JOIN films_directors fd " +
                        "ON fd.film_id = f.film_id " +
                        "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                        "WHERE fd.director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(fl.user_id) DESC ";
            } else {
                selectQuery = "SELECT * FROM films f " +
                        "INNER JOIN films_directors fd " +
                        "ON fd.film_id = f.film_id " +
                        "WHERE fd.director_id = ?";
            }
            return jdbcTemplate.query(selectQuery, filmMapper, directorId);
        } else {
            throw new ObjectNotFoundException(String.format("Директор id=%s не найден", directorId));
        }
    }

    public List<Film> searchFilmByNameOrDirector(String query, List<String> by) {
        String selectQuery;
        if (by.size() == 1 && by.contains("title")) {
            selectQuery = "SELECT * FROM films f " +
                    "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                    "WHERE UPPER(f.film_name) LIKE UPPER(?) " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(fl.user_id) DESC ";
            return jdbcTemplate.query(selectQuery, filmMapper, "%" + query + "%");
        } else if (by.size() == 1 && by.contains("director")) {
            selectQuery = "SELECT f.*, d.director_name FROM films f " +
                    "INNER JOIN films_directors fd " +
                    "ON fd.film_id = f.film_id " +
                    "INNER JOIN directors d " +
                    "ON fd.director_id = d.director_id " +
                    "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                    "WHERE UPPER(d.director_name) LIKE UPPER(?) " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(fl.user_id) DESC ";
            return jdbcTemplate.query(selectQuery, filmMapper, "%" + query + "%");
        } else if (by.size() == 2 && by.contains("title") && by.contains("director")) {
            selectQuery = "SELECT f.*, d.director_name FROM films f " +
                    "LEFT JOIN films_directors fd " +
                    "ON fd.film_id = f.film_id " +
                    "LEFT JOIN directors d " +
                    "ON fd.director_id = d.director_id " +
                    "LEFT JOIN films_likes fl on f.film_id = fl.film_id " +
                    "WHERE (UPPER(d.director_name) LIKE UPPER(?) OR UPPER(f.film_name) LIKE UPPER(?)) " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(fl.user_id) DESC ";
            return jdbcTemplate.query(selectQuery, filmMapper, "%" + query + "%", "%" + query + "%");
        } else return Collections.emptyList();
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.* " +
                "FROM films_likes AS fl1 INNER JOIN films_likes AS fl2 ON fl1.film_id = fl2.film_id " +
                "LEFT JOIN films_likes AS fl3 ON fl1.film_id = fl3.film_id " +
                "LEFT JOIN films AS f ON fl1.film_id = f.film_id " +
                "WHERE fl1.user_id = ? " +
                "AND fl2.user_id = ?  " +
                "GROUP BY fl3.film_id, f.film_id " +
                "ORDER BY COUNT (fl3.user_id) DESC;";
        return jdbcTemplate.query(sqlQuery, filmMapper, userId, friendId);
    }

    @Override
    public boolean checkIsObjectInStorage(Film film) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, film.getId()));
    }

    @Override
    public boolean checkIsObjectInStorage(int filmId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId));
    }

    public boolean checkIsFilmHasLikeFromUser(Film film, User user) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, film.getId(), user.getId()));
    }

    public boolean checkIsFilmHasLikeFromUser(int filmId, int userId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId));
    }

    public String deleteFilmById(Integer filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE film_id = ? ";
        jdbcTemplate.update(sqlQuery, filmId);
        return "Фильм film_id=" + filmId + " успешно удален.";
    }
}
