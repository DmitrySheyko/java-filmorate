package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

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
    private Set<Director> directors;
    private int rate;
}
