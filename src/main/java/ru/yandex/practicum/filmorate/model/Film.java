package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = { "id", "name"})
public class Film {
    private static int identificator = 0;
    private int id;
    @NotBlank(message = "Не указанно название фильма")
    private String name;
    @Size(max = 200, message = "Длительность описания должна состалять от 0 до 200 символов")
    private String description;
    @NotNull(message = "Не указана дата релиза")
    private String releaseDate;
    @Min(1)
    private long duration;
    private int rate;
    private Set<Integer> setOfLikes;

    public void generateAndSetId() {
        setId(++identificator);
    }

    public void generateSetOfLikes() {
        this.setOfLikes = new HashSet<>();
    }

    public void addLike(int userId) {
        setOfLikes.add(userId);
    }

    public void deleteLike(int userId) {
        setOfLikes.remove(userId);
    }
}
