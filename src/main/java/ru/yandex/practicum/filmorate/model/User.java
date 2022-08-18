package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class User {
    private static int identificator = 0;
    private int id;
    private String email;
    private String login;
    private String name;
    private String birthday;
    private Set<Integer> setOfFriends;

    public void generateAndSetId() {
        setId(++identificator);
    }

    public void addFriend(int friendId) {
        setOfFriends.add(friendId);
    }

    public void deleteFriend(Integer friend) {
        setOfFriends.remove(friend);
    }

    public void generateSetOfFriends() {
        this.setOfFriends = new HashSet<>();
    }

    public Set<Integer> getSetOfFriends() {
        return setOfFriends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


