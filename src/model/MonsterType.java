package model;

import java.util.Objects;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents a Monster type catalog entry (e.g. Vampire, Werewolf).
 * @author Fatima
 * @version 1.0
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

    @Override public int getId()            { return id; }
    @Override public void setId(int id)     { this.id = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public String getWeakness()             { return weakness; }
    public void setWeakness(String w)       { this.weakness = w; }
    public int getTerrorLevel()             { return terrorLevel; }
    public void setTerrorLevel(int lvl)     { this.terrorLevel = lvl; }

    @Override
    public String toCsv() {
        return id + "," + name + "," + description + "," + weakness + "," + terrorLevel;
    }

    @Override
    public String toString() {
        return "MonsterType[id=" + id + ", name=" + name + ", terrorLevel=" + terrorLevel + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonsterType)) return false;
        return id == ((MonsterType) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}