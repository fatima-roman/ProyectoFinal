package exceptions;

/**
 * Thrown when a monster type name is not valid or does not exist in the catalogue.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class InvalidMonsterTypeException extends Exception {

    /**
     * Constructs the exception with the invalid type name.
     *
     * @param typeName the monster type name that was rejected
     */
    public InvalidMonsterTypeException(String typeName) {
        super("Monster type \"" + typeName + "\" is not valid or does not exist in the catalogue.");
    }
}
