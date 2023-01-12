package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Review {

    private int reviewId;
    @NotBlank
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    @Builder.Default
    private Integer useful = 0;
}
