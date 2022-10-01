package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Review {
    private int id;
    private Integer userId;
    private Integer filmId;
    private Integer filmdfd;
    private Boolean isPositive;
    private String description;
}

