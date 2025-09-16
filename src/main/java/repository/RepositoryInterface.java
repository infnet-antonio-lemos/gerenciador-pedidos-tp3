package repository;

import java.util.List;

public interface RepositoryInterface<T> {
    T create(T entity);
    T update(T entity);
    List<T> list();
    T get(int id);
    void delete(int id);
}
