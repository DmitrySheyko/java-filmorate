package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//TODO написать индексы к таблицам
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
}
