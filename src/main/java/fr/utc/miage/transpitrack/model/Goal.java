package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.Temporality;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a training goal set by a user.
 * <p>
 * A goal has a target distance (in kilometres), a description text, a sport, and a
 * {@link Temporality} that defines how often the goal resets (daily, weekly, etc.).
 * </p>
 */
@Entity
@Table(name="goal")
public class Goal {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "goal_seq")
    private Long id;

    /** The target distance to reach, in kilometres. */
    private Double targetDistance;

    /** Free-text description of what the user wants to achieve. */
    private String goalText;

    /** The user who owns this goal. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The sport to which this goal applies. */
    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    /** Recurrence period after which the goal resets. */
    @Enumerated(EnumType.STRING)
    private Temporality temporality;

    /**
     * No-argument constructor required by JPA.
     */
    public Goal(){}

    /**
     * Constructs a goal without sport and temporality (legacy constructor).
     *
     * @param targetDistance the target distance in kilometres
     * @param goalText       the description text
     * @param user           the user who owns the goal
     */
    public Goal(Double targetDistance, String goalText, User user) {
        this.targetDistance = targetDistance;
        this.user = user;
        this.goalText = goalText;
    }

    /**
     * Constructs a fully populated goal.
     *
     * @param targetDistance the target distance in kilometres
     * @param goalText       the description text
     * @param user           the user who owns the goal
     * @param sport          the sport targeted by the goal
     * @param temporality    the recurrence period
     */
    public Goal(Double targetDistance, String goalText, User user, Sport sport, Temporality temporality) {
        this.targetDistance = targetDistance;
        this.goalText = goalText;
        this.user = user;
        this.sport = sport;
        this.temporality = temporality;
    }

    /**
     * Returns the target distance for this goal.
     *
     * @return the target distance in kilometres
     */
    public Double getTargetDistance() {
        return targetDistance;
    }

    /**
     * Sets the target distance for this goal.
     *
     * @param targetDistance the target distance in kilometres
     */
    public void setTargetDistance(Double targetDistance) {
        this.targetDistance = targetDistance;
    }

    /**
     * Returns the description text of this goal.
     *
     * @return the goal description
     */
    public String getGoalText() {
        return goalText;
    }

    /**
     * Sets the description text of this goal.
     *
     * @param goalText the goal description
     */
    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    /**
     * Returns the user who owns this goal.
     *
     * @return the owner {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns this goal.
     *
     * @param user the owner {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the unique identifier of this goal.
     *
     * @return the goal ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the sport targeted by this goal.
     *
     * @param sport the associated {@link Sport}
     */
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    /**
     * Sets the recurrence period for this goal.
     *
     * @param temporality the {@link Temporality} value
     */
    public void setTemporality(Temporality temporality) {
        this.temporality = temporality;
    }

    /**
     * Returns the sport targeted by this goal.
     *
     * @return the associated {@link Sport}
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * Returns the recurrence period of this goal.
     *
     * @return the {@link Temporality} value
     */
    public Temporality getTemporality() {
        return temporality;
    }
}
