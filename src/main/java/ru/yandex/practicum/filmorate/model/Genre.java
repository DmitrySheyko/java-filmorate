package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class of entity {@link Genre}.
 *
 * @author DmitrySheyko
 */
@Data
@Builder
public class Genre {

    private int id;
    private String name;

}
