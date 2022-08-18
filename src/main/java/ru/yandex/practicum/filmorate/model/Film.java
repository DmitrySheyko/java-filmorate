package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private static int identificator = 0;
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private long duration;
    private int rate;
    private Set<Integer> setOfLikes;

    public void generateAndSetId(){
        setId(++identificator);
    }

    public void generateSetOfLikes() {
        this.setOfLikes = new HashSet<>();
    }

    public void addLike(int userId){
        setOfLikes.add(userId);
    }

    public void deleteLike (int userId){
        setOfLikes.remove(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
