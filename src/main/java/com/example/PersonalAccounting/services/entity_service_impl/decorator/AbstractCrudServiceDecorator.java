package com.example.PersonalAccounting.services.entity_service_impl.decorator;

import com.example.PersonalAccounting.services.CrudService;

import java.util.List;

public abstract class AbstractCrudServiceDecorator<T> implements CrudService<T> {

    protected CrudService<T> service;

    protected AbstractCrudServiceDecorator(CrudService<T> service) {
        this.service = service;
    }


    @Override
    public T create(T toCreate) {
        return service.create(toCreate);
    }

    @Override
    public List<T> getAll() {
        return service.getAll();
    }

    @Override
    public T getOne(int id) {
        return service.getOne(id);
    }

    @Override
    public T update(int id, T updated) {
        return service.update(id, updated);
    }

    @Override
    public void delete(int id) {
        service.delete(id);
    }
}
