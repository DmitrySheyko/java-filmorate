package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
