package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private static int identificator = 0;
    private int id;
    @Email(message = "Указан некорректный email")
    private String email;
    @NotBlank(message = "Не указан login")
    private String login;
    private String name;
    @NotBlank(message = "Не указана дата рождения")
    private String birthday;
    private Set<Integer> setOfFriends;

    public void generateAndSetId() {
        setId(++identificator);
    }

    public void generateSetOfFriends() {
        this.setOfFriends = new HashSet<>();
    }

    public void addFriend(int friendId) {
        setOfFriends.add(friendId);
    }

    public void deleteFriend(int friend) {
        setOfFriends.remove(friend);
    }
}


