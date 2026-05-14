package exceptions;

public class InvalidMonsterTypeException extends Exception {
    public InvalidMonsterTypeException(String typeName) {
        super("Monster type \"" + typeName + "\" is not valid or does not exist in the catalogue.");
    }
}
