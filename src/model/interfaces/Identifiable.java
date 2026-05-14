package model.interfaces;

/**
 * Interface that guarantees an entity can be identified by an integer ID.
 * @author Fátima Román
 * @version 1.0
 */
public interface Identifiable {
    int getId();
    void setId(int id);
}
