package model.interfaces;

/**
 * Contract for entities that can be graded and evaluated.
 *
 * @author Fátima Román
 * @version 1.1
 */
public interface Evaluable {

    /**
     * Calculates and returns the entity's final grade.
     *
     * @return calculated final grade
     */
    double calculateFinalGrade();

    /**
     * Indicates whether the entity has passed the evaluation (grade &ge; 5).
     *
     * @return {@code true} if it has passed
     */
    boolean hasPassed();
}