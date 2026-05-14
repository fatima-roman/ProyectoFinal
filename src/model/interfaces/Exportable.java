package model.interfaces;

/**
 * Interface for entties that can be serialized to a CSV row.
 * @author Fátima
 * @version 1.0
 */
public interface Exportable {
    String toCsv();
}
