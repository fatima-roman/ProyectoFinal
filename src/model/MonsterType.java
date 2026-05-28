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

    private int id;
    private String name;
    private String description;
    private String weakness;
    private int terrorLevel;

    public MonsterType(int id, String name, String description, String weakness, int terrorLevel) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.weakness    = weakness;
        this.terrorLevel = terrorLevel;
    }

    @Override public int getId(){ return id; }
    @Override public void setId(int id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getDescription(){ return description; }
    public void setDescription(String d){ this.description = d; }
    public String getWeakness(){ return weakness; }
    public void setWeakness(String w){ this.weakness = w; }
    public int getTerrorLevel(){ return terrorLevel; }
    public void setTerrorLevel(int lvl){ this.terrorLevel = lvl; }

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