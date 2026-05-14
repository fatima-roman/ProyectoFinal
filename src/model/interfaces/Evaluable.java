package model.interfaces;

/**
 * Interface for entities that can be graded and evaluated.
 * @author Fatima
 * @version 1.0
 */
public interface Evaluable {
    double calculateFinalGrade();
    boolean hasPassed();
}