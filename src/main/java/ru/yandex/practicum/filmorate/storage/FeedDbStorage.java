package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
@Slf4j
public class FeedDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedMapper feedMapper;

    public Feed add(Integer entityId, int typeId, int operationId, Integer userId) {
        String sqlQuery = "INSERT INTO feeds (entity_id,user_id,event_type_id,operation_type_id,timestamp) " +
                "VALUES(?,?,?,?,?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setInt(1, entityId);
            stmt.setInt(2, userId);
            stmt.setInt(3, typeId);
            stmt.setInt(4, operationId);
            stmt.setLong(5, Instant.now().toEpochMilli());
            return stmt;
        }, keyHolder);
        return getById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    public List<Feed> getByUserId(Integer userId) {
        String sqlQuery =
                "SELECT f.event_id,f.entity_id,f.user_id,et.event_type,o.operation_type,f.timestamp " +
                        "FROM feeds AS f " +
                        "LEFT JOIN event_types AS et ON et.event_type_id = f.event_type_id " +
                        "LEFT JOIN operations AS o ON o.operation_type_id = f.operation_type_id " +
                        "WHERE f.user_id=?";
        return jdbcTemplate.query(sqlQuery, feedMapper, userId);
    }

    public Feed getById(Integer feedId) {
        String sqlQuery =
                "SELECT f.event_id,f.entity_id,f.user_id,et.event_type,o.operation_type,f.timestamp " +
                        "FROM feeds AS f " +
                        "LEFT JOIN event_types AS et ON et.event_type_id = f.event_type_id " +
                        "LEFT JOIN operations AS o ON o.operation_type_id = f.operation_type_id " +
                        "WHERE f.event_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, feedMapper, feedId);
    }

}
