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

    /** Unique identifier of the person. */
    private int id;

    /** First name. */
    private String name;

    /** Surname. */
    private String surname;

    /** Birth date; may be {@code null}. */
    private LocalDate birthDate;

    /** Email address. */
    private String email;

    /**
     * Full constructor.
     *
     * @param id        unique identifier
     * @param name      first name
     * @param surname   surname
     * @param birthDate birth date
     * @param email     email address (must contain '@')
     */
    public Person(int id, String name, String surname, LocalDate birthDate, String email) {
        this.id        = id;
        this.name      = name;
        this.surname   = surname;
        this.birthDate = birthDate;
        this.email     = email;
    }

    /**
     * Returns a description of the role this person plays in the institute.
     *
     * @return descriptive string of the role
     */
    public abstract String getRoleDescription();

    /** {@inheritDoc} */
    @Override
    public int getId() { return id; }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) { this.id = id; }

    /**
     * Returns the first name.
     *
     * @return first name
     */
    public String getName() { return name; }

    /**
     * Sets the first name.
     *
     * @param name new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the surname.
     *
     * @return surname
     */
    public String getSurname() { return surname; }

    /**
     * Sets the surname.
     *
     * @param surname new surname
     */
    public void setSurname(String surname) { this.surname = surname; }

    /**
     * Returns the birth date.
     *
     * @return birth date, or {@code null} if it is not registered
     */
    public LocalDate getBirthDate() { return birthDate; }

    /**
     * Sets the birth date.
     *
     * @param birthDate new birth date
     */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    /**
     * Returns the email.
     *
     * @return email
     */
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