package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Review {
    private int id;
    private Integer userId;
    private Integer filmId;
    private Boolean isPositive;
    @NotBlank
    private String content;
    @Builder.Default
    private Integer useful = 0;
}
