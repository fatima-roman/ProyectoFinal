package model;

import java.time.LocalDate;
import java.util.Objects;
import model.interfaces.Identifiable;

/**
 * Abstract base class that represents any person in the Monster High Institute.
 * <p>
 * All attributes are {@code private} to guarantee encapsulation;
 * subclasses access them exclusively through the inherited getters and setters.
 * </p>
 *
 * @author Fátima Román
 * @version 1.1
 */
public abstract class Person implements Identifiable {

    private int id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;

    public Person(int id, String name, String surname, LocalDate birthDate, String email) {
        this.id        = id;
        this.name      = name;
        this.surname   = surname;
        this.birthDate = birthDate;
        this.email     = email;
    }

    public abstract String getRoleDescription();
    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getEmail() { return email; }
    /**
     * Sets the email, validating that it contains '@'.
     *
     * @param email new email
     * @throws IllegalArgumentException if the format is invalid
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email format: " + email);
        this.email = email;
    }

    /**
     * Returns a human-readable representation of the person.
     *
     * @return string with class, id, full name and email
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "[id=" + id + ", name=" + name + " " + surname + ", email=" + email + "]";
    }

    /**
     * Two people are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if the ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        return id == ((Person) o).id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(id); }
}