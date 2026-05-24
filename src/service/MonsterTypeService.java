package service;

import exceptions.InvalidMonsterTypeException;
import model.MonsterType;
import repository.MonsterTypeDAO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service for monster type catalogue management.
 * Acts as the middle layer between the UI and {@link MonsterTypeDAO},
 * enforcing business rules before any database access.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MonsterTypeService {

    /** DAO used to interact with the MONSTER_TYPE table. */
    private final MonsterTypeDAO monsterTypeDAO = new MonsterTypeDAO();

    /**
     * Returns all monster types stored in the catalogue.
     *
     * @return list of all {@link MonsterType} entries (may be empty, never {@code null})
     */
    public List<MonsterType> findAll() {
        return monsterTypeDAO.findAll();
    }

    /**
     * Finds a monster type by its primary key.
     *
     * @param id the monster type identifier
     * @return the matching {@link MonsterType}
     * @throws InvalidMonsterTypeException if no monster type exists with that ID
     */
    public MonsterType findById(int id) throws InvalidMonsterTypeException {
        MonsterType mt = monsterTypeDAO.findById(id);
        if (mt == null) throw new InvalidMonsterTypeException("ID " + id);
        return mt;
    }

    /**
     * Persists a new monster type after validating its fields.
     *
     * @param mt the monster type to create; must not be {@code null}
     * @throws IllegalArgumentException if name is blank or terror level is out of range
     */
    public void save(MonsterType mt) {
        validate(mt);
        monsterTypeDAO.save(mt);
    }

    /**
     * Updates an existing monster type record after validation.
     *
     * @param mt the monster type with updated values; must not be {@code null}
     * @throws InvalidMonsterTypeException if no monster type exists with that ID
     * @throws IllegalArgumentException    if validation fails
     */
    public void update(MonsterType mt) throws InvalidMonsterTypeException {
        findById(mt.getId());
        validate(mt);
        monsterTypeDAO.update(mt);
    }

    /**
     * Deletes a monster type by its ID.
     *
     * @param id the monster type identifier to delete
     * @throws InvalidMonsterTypeException if no monster type exists with that ID
     */
    public void deleteById(int id) throws InvalidMonsterTypeException {
        findById(id);
        monsterTypeDAO.deleteById(id);
    }

    /**
     * Returns all monster types sorted by name ascending.
     *
     * @return sorted list of monster types
     */
    public List<MonsterType> findAllSortedByName() {
        return monsterTypeDAO.findAll().stream()
                .sorted(Comparator.comparing(MonsterType::getName))
                .collect(Collectors.toList());
    }

    /**
     * Returns all monster types whose terror level is at or above the given threshold.
     *
     * @param minLevel the minimum terror level (inclusive)
     * @return filtered list of monster types
     */
    public List<MonsterType> findByMinTerrorLevel(int minLevel) {
        return monsterTypeDAO.findAll().stream()
                .filter(mt -> mt.getTerrorLevel() >= minLevel)
                .collect(Collectors.toList());
    }

    /**
     * Returns the names of all monster types.
     *
     * @return list of monster type names
     */
    public List<String> getAllNames() {
        return monsterTypeDAO.findAll().stream()
                .map(MonsterType::getName)
                .collect(Collectors.toList());
    }

    /**
     * Returns the average terror level across all monster types.
     *
     * @return average terror level, or 0.0 if the catalogue is empty
     */
    public double averageTerrorLevel() {
        return monsterTypeDAO.findAll().stream()
                .mapToInt(MonsterType::getTerrorLevel)
                .average()
                .orElse(0.0);
    }

    /**
     * Validates that the monster type has a non-blank name and a terror level in [1, 10].
     *
     * @param mt the monster type to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validate(MonsterType mt) {
        if (mt.getName() == null || mt.getName().isBlank())
            throw new IllegalArgumentException("Monster type name cannot be blank.");
        if (mt.getTerrorLevel() < 1 || mt.getTerrorLevel() > 10)
            throw new IllegalArgumentException("Terror level must be between 1 and 10.");
    }
}
