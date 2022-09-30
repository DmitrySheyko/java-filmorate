package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres;
    private Integer rate;
    private Integer dfgdfgd;
}
