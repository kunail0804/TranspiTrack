package fr.utc.miage.transpitrack.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * JPA entity representing a sport available in the application.
 * <p>
 * Each sport belongs to a {@link SportType} category and carries a MET
 * (Metabolic Equivalent of Task) value used to estimate calorie expenditure.
 * </p>
 */
@Entity
public class Sport {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sport_seq")
    private Long id;

    /** Display name of the sport. */
    private String name;

    /** Short description of the sport. */
    private String description;

    /**
     * MET (Metabolic Equivalent of Task) value for this sport.
     * Used in the calorie formula: {@code calories = MET × weight (kg) × duration (h)}.
     */
    private double metValue;

    /** Users who have selected this sport as a preference. */
    @OneToMany(mappedBy = "sport")
    private List<UserSport> users = new ArrayList<>();

    /** Category to which this sport belongs. */
    @ManyToOne
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;

    /**
     * No-argument constructor required by JPA.
     */
    public Sport() {
    }

    /**
     * Constructs a {@code Sport} with a name and description.
     *
     * @param name        the display name of the sport
     * @param description a short description
     */
    public Sport(String name, String description){
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the unique identifier of this sport.
     *
     * @return the sport ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the display name of this sport.
     *
     * @return the sport name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this sport.
     *
     * @param name the sport name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this sport.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this sport.
     *
     * @param description the description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the user sport preferences associated with this sport.
     *
     * @return the list of {@link UserSport} entries
     */
    public List<UserSport> getUsers() {
        return users;
    }

    /**
     * Sets the user sport preferences associated with this sport.
     *
     * @param users the list of {@link UserSport} entries
     */
    public void setUsers(List<UserSport> users) {
        this.users = users;
    }

    /**
     * Returns the MET value of this sport.
     *
     * @return the MET value
     */
    public double getMetValue() {
        return metValue;
    }

    /**
     * Sets the MET value of this sport.
     *
     * @param metValue the MET value
     */
    public void setMetValue(double metValue) {
        this.metValue = metValue;
    }

    /**
     * Returns the category to which this sport belongs.
     *
     * @return the associated {@link SportType}
     */
    public SportType getSportType() {
        return sportType;
    }

    /**
     * Sets the category for this sport.
     *
     * @param sportType the associated {@link SportType}
     */
    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }
}
