package ru.yandex.practicum.filmorate.controller;

import java.util.List;

public interface Controllers<T> {
    List<T> getAll();

    T getById(int id);

    T add(T obj);

    T update(T obj);
}
