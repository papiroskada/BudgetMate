package com.example.PersonalAccounting.services;

import java.util.List;

public interface CrudService<T> {

    T create(T toCreate);

    List<T> getAll();

    T getOne(int id);

    T update(int id, T updated);

    void delete(int id);
}
