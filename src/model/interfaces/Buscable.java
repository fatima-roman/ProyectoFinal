package model.interfaces;

/**
 * Contract for entities that support text search by keyword.
 *
 * @author Fátima Román
 * @version 1.1
 */
public interface Buscable {

    /**
     * Returns {@code true} if this entity matches the given keyword
     * (case-insensitive).
     *
     * @param keyword search string
     * @return {@code true} if there is a match
     */
    boolean matches(String keyword);
}