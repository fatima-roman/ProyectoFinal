package model.interfaces;

/**
 * Contract that guarantees an entity can be identified by an integer ID.
 *
 * @author Fátima Román
 * @version 1.1
 */
public interface Identifiable {

    /**
     * Returns the entity's unique identifier.
     *
     * @return integer identifier
     */
    int getId();

    /**
     * Sets the entity's unique identifier.
     *
     * @param id new identifier
     */
    void setId(int id);
}