package ru.yandex.practicum.filmorate.mapper;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@AllArgsConstructor
public class FilmMapper implements RowMapper<Film> {
    private final DirectorDbStorage directorDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getString("release_date"))
                .duration(rs.getInt("duration"))
                .mpa(mpaDbStorage.getById(rs.getInt("rating")))
                .genres(genreDbStorage.findGenresOfFilm(rs.getInt("film_id")))
                .directors(directorDbStorage.getDirectorsByFilmId(rs.getInt("film_id")))
                .build();
    }
}
