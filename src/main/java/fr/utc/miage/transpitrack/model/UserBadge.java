package fr.utc.miage.transpitrack.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * JPA join entity recording which {@link Badge} a {@link User} has earned and when.
 * <p>
 * A {@code UserBadge} is created by {@link fr.utc.miage.transpitrack.model.jpa.BadgeService}
 * when a user's accumulated statistics cross a badge's threshold value.
 * </p>
 */
@Entity
public class UserBadge {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who earned the badge. */
    @ManyToOne
    private User user;

    /** The badge that was earned. */
    @ManyToOne
    private Badge badge;

    /** The date on which the badge was awarded. */
    private LocalDate earnedAt;

    /**
     * No-argument constructor required by JPA.
     */
    public UserBadge() {}

    /**
     * Constructs a {@code UserBadge} recording the award event.
     *
     * @param user     the user who earned the badge
     * @param badge    the badge that was earned
     * @param earnedAt the date the badge was awarded
     */
    public UserBadge(User user, Badge badge, LocalDate earnedAt) {
        this.user = user;
        this.badge = badge;
        this.earnedAt = earnedAt;
    }

    /**
     * Returns the unique identifier of this record.
     *
     * @return the user-badge record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user who earned the badge.
     *
     * @return the associated {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the badge that was earned.
     *
     * @return the associated {@link Badge}
     */
    public Badge getBadge() {
        return badge;
    }

    /**
     * Returns the date the badge was awarded.
     *
     * @return the award date
     */
    public LocalDate getEarnedAt() {
        return earnedAt;
    }

    /**
     * Sets the user who earned the badge.
     *
     * @param user the associated {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the badge that was earned.
     *
     * @param badge the associated {@link Badge}
     */
    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    /**
     * Sets the date the badge was awarded.
     *
     * @param earnedAt the award date
     */
    public void setEarnedAt(LocalDate earnedAt) {
        this.earnedAt = earnedAt;
    }
}
