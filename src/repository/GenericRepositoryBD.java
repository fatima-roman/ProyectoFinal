package repository;

import java.util.List;

import model.interfaces.Identifiable;

/**
 * Generic base repository with CRUD operations.
 * @param u entity type implementing Identifiable
 * @author Fatima R
 * @version 1.0
 */
public abstract class GenericRepositoryBD<u extends Identifiable> {
    public abstract void save(u entity);
    public abstract void update(u entity);
    public abstract void deleteById(int id);
    public abstract u findById(int id);
    public abstract List<u> findAll();
}
