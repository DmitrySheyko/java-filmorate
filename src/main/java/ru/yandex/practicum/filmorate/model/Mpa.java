package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class of entity {@link Mpa}.
 *
 * @author DmitrySheyko
 */
@Data
@Builder
public class Mpa {

    private int id;
    private String name;

}
