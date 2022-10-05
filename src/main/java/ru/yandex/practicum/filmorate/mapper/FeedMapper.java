package ru.yandex.practicum.filmorate.mapper;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@AllArgsConstructor
public class FeedMapper implements RowMapper<Feed> {

    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .entityId(rs.getInt("entity_id"))
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation_type"))
                .timestamp(rs.getLong("timestamp"))
                .build();
    }
}
