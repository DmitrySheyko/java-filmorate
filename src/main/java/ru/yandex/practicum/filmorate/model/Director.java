package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {
    private int id;
    @NonNull
    @NotBlank
    private String name;
}
