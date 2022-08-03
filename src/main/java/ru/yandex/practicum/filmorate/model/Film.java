package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Film {
    private static int identificator = 0;
    private int id = setId();
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotBlank
    private String releaseDate;
    @Min(1)
    private long duration;

    public int setId() {
        return ++identificator;
    }
}
