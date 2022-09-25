package com.openjob.admin.base;

import com.openjob.admin.exception.AdminUserNotFound;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractBaseService<T> {
    public abstract Optional<T> get(String id);
    public abstract Collection<T> getAll();
    public abstract T save(T object);
    public abstract void delete(String id);
    public abstract void activate(String id) throws AdminUserNotFound;
    public abstract void deactivate(String id) throws AdminUserNotFound;

}
