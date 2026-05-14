package repository;

import java.util.List;

import model.interfaces.Identifiable;

/**
 * Generic base repository with CRUD operations.
 * @param u entity type implementing Identifiable
 * @author Fatima R
 * @version 1.1
 */
public abstract class GenericRepositoryBD<U> {
    public abstract void save(U entity);
    public abstract void update(U entity);
    public abstract void deleteById(int id);
    public abstract U findById(int id);
    public abstract List<U> findAll();
}
