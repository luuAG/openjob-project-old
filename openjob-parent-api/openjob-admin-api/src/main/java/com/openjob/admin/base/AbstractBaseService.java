package com.openjob.admin.base;

import java.sql.SQLException;
import java.util.Optional;

public abstract class AbstractBaseService<T> {
    public abstract Optional<T> get(String id);
    public abstract T save(T object) throws SQLException;
    public abstract void delete(String id);

}
