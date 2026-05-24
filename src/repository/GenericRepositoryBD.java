package repository;

import java.util.List;
import model.interfaces.Identifiable;

/**
 * Generic abstract base repository providing standard CRUD operations.
 * All DAO classes must extend this class using a concrete type parameter
 * that implements {@link Identifiable}.
 *
 * @param <T> the entity type, must implement {@link Identifiable}
 * @author Fatima Roman
 * @version 1.2
 */
public abstract class GenericRepositoryBD<T extends Identifiable> {

    /**
     * Persists a new entity to the database.
     *
     * @param entity the entity to save; must not be {@code null}
     */
    public abstract void save(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity with updated values; must not be {@code null}
     */
    public abstract void update(T entity);

    /**
     * Deletes the entity with the given ID from the database.
     *
     * @param id the identifier of the entity to delete
     */
    public abstract void deleteById(int id);

    /**
     * Finds an entity by its primary key.
     *
     * @param id the identifier to look up
     * @return the matching entity, or {@code null} if not found
     */
    public abstract T findById(int id);

    /**
     * Returns all entities stored in the database.
     *
     * @return list of all entities (may be empty, never {@code null})
     */
    public abstract List<T> findAll();
}
