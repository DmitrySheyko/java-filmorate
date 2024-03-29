package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class of entity {@link Feed}.
 *
 * @author DmitrySheyko
 */
@Data
@Builder
public class Feed {

    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer eventId;
    private Integer entityId;

}
