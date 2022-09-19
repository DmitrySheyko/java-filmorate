package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getString("release_date"))
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("rating"))
                        .name(rs.getString("rating_name"))
                        .build())
                .genres(createGenreListFromSting(rs.getString("genre_id_name")))
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
            return listOfGenres;
        } else {
            return null;
        }
    }
}
