package model;

import java.util.Objects;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents a monster type from the Monster High Institute catalog
 * (e.g. Vampire, Werewolf, Zombie).
 *
 * @author Fátima Román
 * @version 1.1
 */
public class MonsterType implements Identifiable, Exportable {

    /** Unique identifier. */
    private int id;

    /** Name of the monster type. */
    private String name;

    /** Short description of the monster type. */
    private String description;

    /** Main weakness of the monster type. */
    private String weakness;

    /** Terror level on a 1–10 scale. */
    private int terrorLevel;

    /**
     * Full constructor.
     *
     * @param id          unique identifier
     * @param name        type name
     * @param description short description
     * @param weakness    main weakness
     * @param terrorLevel terror level (1-10)
     */
    public MonsterType(int id, String name, String description, String weakness, int terrorLevel) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.weakness    = weakness;
        this.terrorLevel = terrorLevel;
    }

    /**
     * Copy constructor.
     *
     * @param other monster type to copy
     */
    public MonsterType(MonsterType other) {
        this(other.id, other.name, other.description, other.weakness, other.terrorLevel);
    }

    /** {@inheritDoc} */
    @Override public int getId()              { return id; }

    /** {@inheritDoc} */
    @Override public void setId(int id)       { this.id = id; }

    /**
     * Returns the type name.
     * @return name
     */
    public String getName()                   { return name; }

    /**
     * Sets the type name.
     * @param name new name
     */
    public void setName(String name)          { this.name = name; }

    /**
     * Returns the description.
     * @return description
     */
    public String getDescription()            { return description; }

    /**
     * Sets the description.
     * @param d new description
     */
    public void setDescription(String d)      { this.description = d; }

    /**
     * Returns the weakness.
     * @return weakness
     */
    public String getWeakness()               { return weakness; }

    /**
     * Sets the weakness.
     * @param w new weakness
     */
    public void setWeakness(String w)         { this.weakness = w; }

    /**
     * Returns the terror level.
     * @return terror level (1-10)
     */
    public int getTerrorLevel()               { return terrorLevel; }

    /**
     * Sets the terror level.
     * @param lvl new level (1-10)
     */
    public void setTerrorLevel(int lvl)       { this.terrorLevel = lvl; }

    /**
     * Exports this monster type as a CSV line: id,name,description,weakness,terrorLevel.
     *
     * @return CSV representation
     */
    @Override
    public String toCsv() {
        return id + "," + name + "," + description + "," + weakness + "," + terrorLevel;
    }

    /**
     * Returns a human-readable representation of this monster type.
     *
     * @return string with id, name and terror level
     */
    @Override
    public String toString() {
        return "MonsterType[id=" + id + ", name=" + name + ", terrorLevel=" + terrorLevel + "]";
    }

    /**
     * Two monster types are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if the ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonsterType)) return false;
        return id == ((MonsterType) o).id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(id); }
}