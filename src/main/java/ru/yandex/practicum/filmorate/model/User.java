package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class of entity {@link User}.
 *
 * @author DmitrySheyko
 */
@Data
@Builder
public class User {

    private int id;
    private String email;
    private String login;
    private String name;
    private String birthday;

}


