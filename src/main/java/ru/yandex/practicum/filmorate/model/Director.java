package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class of entity {@link Director}.
 *
 * @author DmitrySheyko
 */
@Data
@Builder
public class Director {
    private int id;
    private String name;
}
