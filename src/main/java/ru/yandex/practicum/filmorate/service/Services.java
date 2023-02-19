package ru.yandex.practicum.filmorate.service;

import java.util.List;

/**
 * Interface of services classes.
 *
 * @author DmitrySheyko
 */
public interface Services<T> {

    List<T> getAll();

    T getById(int id);

    T add(T obj);

    T update(T obj);

}
