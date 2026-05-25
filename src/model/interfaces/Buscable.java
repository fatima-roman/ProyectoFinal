package model.interfaces;

/**
 * Contract for entities that support text-based keyword search.
 * @author Fatima Roman
 * @version 1.0
 */
public interface Buscable {
    /**
     * Returns {@code true} if this entity matches the given keyword (case-insensitive).
     * @param keyword the search string
     * @return {@code true} on match
     */
    boolean matches(String keyword);
}