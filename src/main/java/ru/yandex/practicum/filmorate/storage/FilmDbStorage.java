package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        String sqlQuery = "select " +
                "f.FILM_ID," +
                "f.FILM_NAME," +
                "f.DESCRIPTION," +
                "f.DURATION," +
                "f.RELEASE_DATE," +
                "f.RATING, " +
                "string_agg(cast(fg.GENRE_ID as varchar), ',') as genre_id " +
                "from films as f " +
                "left join FILMS_GENRES as fg " +
                "on f.FILM_ID = fg.FILM_ID " +
                "group by f.FILM_ID, " +
                "f.FILM_NAME," +
                "f.DESCRIPTION," +
                "f.DURATION," +
                "f.RELEASE_DATE," +
                "f.RATING ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film newFilm) {
        String sqlQuery = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING ) values (?, ?, ?, ?, ?)";
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
                String sqlQueryForGenres = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
                jdbcTemplate.update(sqlQueryForGenres, newFilm.getId(), genre.getId());
            }
        }
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (checkIsFilmInStorage(updatedFilm)) {
            String sqlQuery = "update FILMS set " +
                    "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING = ? " +
                    "where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery
                    , updatedFilm.getName()
                    , updatedFilm.getDescription()
                    , updatedFilm.getReleaseDate()
                    , updatedFilm.getDuration()
                    , updatedFilm.getMpa().getId()
                    , updatedFilm.getId());
            return updatedFilm;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%s не найден.", updatedFilm.getId()));
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "insert into FILMS_LIKES ( FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userid) {
        String sqlQuery = "delete from FILMS_LIKES where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userid);
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "select " +
                "f.FILM_ID," +
                "f.FILM_NAME," +
                "f.DESCRIPTION," +
                "f.DURATION," +
                "f.RELEASE_DATE," +
                "f.RATING, " +
                "r.rating_name," +
                "string_agg(cast(fg.GENRE_ID as varchar), ',') as genre_id " +
                "from films as f " +
                "left join FILMS_GENRES as fg " +
                "on f.FILM_ID = fg.FILM_ID " +
                "left join RATINGS as r " +
                "on f.RATING = R.RATING_ID " +
                "where f.FILM_ID = ?" +
                "group by f.FILM_ID, " +
                "f.FILM_NAME," +
                "f.DESCRIPTION," +
                "f.DURATION," +
                "f.RELEASE_DATE," +
                "f.RATING," +
                "r.RATING_NAME";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
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
                .genres(createGenreListFromSting(resultSet.getString("genre_id")))
                .build();
    }

    private List<Genre> createGenreListFromSting(String rowStringGenres) {
        if (rowStringGenres != null) {
            String[] genresId = rowStringGenres.split(",");
            ArrayList<Genre> listOfGenres = new ArrayList<>(genresId.length);
            for (String genreId : genresId) {
                listOfGenres.add(Genre.builder()
                        .id(Integer.parseInt(genreId))
//                   .name(null)
                        .build());
            }
            System.out.println("List of genres: " + listOfGenres);
            return listOfGenres;
        } else {
            return null;
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT fd.film_id, " +
                "fd.film_name, " +
                "fd.description, " +
                "fd.duration, " +
                "fd.release_date, " +
                "fd.rating," +
                "rating_name, " +
                "genre_id" +
                " FROM ( SELECT f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.duration, " +
                "f.release_date, " +
                "f.rating, " +
                "rating_name," +
                "STRING_AGG(cast (fg.genre_id as varchar), ',') as genre_id " +
                "FROM films AS f " +
                "LEFT JOIN films_genres AS fg " +
                "ON f.film_id=fg.film_id " +
                "left join RATINGS as r " +
                "on f.rating=r.rating_id " +
                "GROUP BY  f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.duration, " +
                "f.release_date, " +
                "f.rating," +
                "r.rating_name) as fd " +
                "LEFT JOIN films_likes AS fl " +
                "ON fd.film_id=fl.film_id " +
                "GROUP BY fd.film_id, " +
                "fd.film_name, " +
                "fd.description, " +
                "fd.duration, " +
                "fd.release_date, " +
                "fd.rating, " +
                "genre_id " +
                "ORDER BY COUNT(*) " +
                "DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    public boolean checkIsFilmInStorage(Film film) {
        String sqlQuery = "select exists (select 1 from films where film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, film.getId());
    }

    public boolean checkIsFilmInStorage(int filmId) {
        String sqlQuery = "select exists (select 1 from films where film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId);
    }
}
