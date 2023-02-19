package ru.yandex.practicum.filmorate.controller;

import java.util.List;

/**
 * Interface for controller classes.
 *
 * @author DmitrySheyko
 */
public interface Controllers<T> {

    List<T> getAll();

    T getById(int id);

    T add(T obj);

    T update(T obj);

}
