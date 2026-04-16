package fr.utc.miage.transpitrack.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * JPA entity representing a category of sports (e.g., "Course à pied", "Natation").
 * <p>
 * Sport types group related {@link Sport} entries under a common label and description,
 * making it easier for users to browse and filter sports in the application.
 * </p>
 */
@Entity
public class SportType {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sportType_seq")
    private Long id;

    /** Display name of the sport category. */
    private String name;

    /** Short description of the category. */
    private String description;

    /** Sports belonging to this category. */
    @OneToMany(mappedBy = "sportType")
    private List<Sport> sports = new ArrayList<>();

    /**
     * No-argument constructor required by JPA.
     */
    public SportType() {
    }

    /**
     * Constructs a fully populated {@code SportType}.
     *
     * @param id          the identifier (used in tests / data loading)
     * @param name        the display name of the category
     * @param description a short description
     * @param sports      the sports belonging to this category
     */
    public SportType(Long id, String name, String description, List<Sport> sports) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sports = sports;
    }

    /**
     * Returns the unique identifier of this sport type.
     *
     * @return the sport type ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the display name of this sport category.
     *
     * @return the name string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this sport category.
     *
     * @param name the name string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this sport category.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this sport category.
     *
     * @param description the description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of sports belonging to this category.
     *
     * @return the list of {@link Sport} entries
     */
    public List<Sport> getSports() {
        return sports;
    }

    /**
     * Sets the list of sports belonging to this category.
     *
     * @param sports the list of {@link Sport} entries
     */
    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }
}
