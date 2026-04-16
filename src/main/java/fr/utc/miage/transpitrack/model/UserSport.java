package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.Level;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA join entity associating a {@link User} with a {@link Sport} and recording
 * the user's self-assessed proficiency {@link Level} for that sport.
 * <p>
 * This entity models the "sport preferences" feature, where users can declare
 * which sports they practise and at what level.
 * </p>
 */
@Entity
@Table(name="userSport")
public class UserSport {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userSport_seq")
    private Long id;

    /** The user who declared this sport preference. */
    @ManyToOne
    private User user;

    /** The sport the user practises. */
    @ManyToOne
    private Sport sport;

    /** The user's self-assessed proficiency level for this sport. */
    @Enumerated(EnumType.STRING)
    private Level level;

    /**
     * Constructs a {@code UserSport} preference.
     *
     * @param user  the user declaring the preference
     * @param sport the sport being declared
     * @param level the user's proficiency level
     */
    public UserSport(User user, Sport sport, Level level) {
        this.user = user;
        this.sport = sport;
        this.level = level;
    }

    /**
     * No-argument constructor required by JPA.
     */
    public UserSport() {
    }

    /**
     * Returns the unique identifier of this preference record.
     *
     * @return the record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user who declared this preference.
     *
     * @return the associated {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the sport this preference refers to.
     *
     * @return the associated {@link Sport}
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * Returns the user's proficiency level for this sport.
     *
     * @return the {@link Level}
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Updates the user's proficiency level for this sport.
     *
     * @param level the new {@link Level}
     */
    public void setLevel(Level level) {
        this.level = level;
    }
}
