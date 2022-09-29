package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storages<T> {

    List<T> getAll();

    T getById(int id);

    T add(T obj);

    T update(T obj);

    boolean checkIsObjectInStorage(T obj);

    boolean checkIsObjectInStorage(int id);
}
