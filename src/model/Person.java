package model;

import java.time.LocalDate;
import java.util.Objects;
import model.interfaces.Identifiable;

/**
 * Abstract base class for all persons in the Monster High Institute.
 * @author Fátima Román 
 * @version 1.0
 */
public abstract class Person implements Identifiable {

    protected int id;
    protected String name;
    protected String surname;
    protected LocalDate birthDate;
    protected String email;

    public Person(int id, String name, String surname, LocalDate birthDate, String email) {
        this.id        = id;
        this.name      = name;
        this.surname   = surname;
        this.birthDate = birthDate;
        this.email     = email;
    }

    /** @return role description string */
    public abstract String getRoleDescription();

    @Override public int getId()          { return id; }
    @Override public void setId(int id)   { this.id = id; }
    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }
    public String getSurname()            { return surname; }
    public void setSurname(String s)      { this.surname = s; }
    public LocalDate getBirthDate()       { return birthDate; }
    public void setBirthDate(LocalDate d) { this.birthDate = d; }
    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "[id=" + id + ", name=" + name + " " + surname + ", email=" + email + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        return id == ((Person) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
