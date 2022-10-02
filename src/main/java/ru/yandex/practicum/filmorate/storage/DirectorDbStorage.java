package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
@AllArgsConstructor
public class DirectorDbStorage implements Storages<Director> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorMapper;

    @Override
    public List<Director> getAll() {
        return null;
    }

    @Override
    public Director getById(int id) {
        return null;
    }

    @Override
    public Director add(Director obj) {
        return null;
    }

    @Override
    public Director update(Director obj) {
        return null;
    }

    @Override
    public boolean checkIsObjectInStorage(Director obj) {
        return false;
    }

    @Override
    public boolean checkIsObjectInStorage(int id) {
        return false;
    }
}
