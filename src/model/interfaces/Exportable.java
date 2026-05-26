package model.interfaces;

/**
 * Contract for entities that can be serialized as a CSV row.
 *
 * @author Fátima Román
 * @version 1.1
 */
public interface Exportable {

    /**
     * Returns this entity's representation as a CSV line.
     *
     * @return CSV line with the entity's fields separated by commas
     */
    String toCsv();
}