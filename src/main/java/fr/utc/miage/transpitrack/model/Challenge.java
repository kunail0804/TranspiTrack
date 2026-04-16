package fr.utc.miage.transpitrack.model;

import java.time.Duration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * JPA entity representing a fitness challenge that users can create and join.
 * <p>
 * A challenge is created by a {@link User} (the creator), targets a specific {@link Sport},
 * has a defined duration, and a visibility setting ({@code "PUBLIC"} or {@code "PRIVATE"}).
 * Other users may join the challenge and submit scores.
 * </p>
 */
@Entity
public class Challenge {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "challenge_seq")
    private Long id;

    /** Display title of the challenge. */
    private String title;

    /**
     * Visibility setting controlling who can see the challenge.
     * Expected values: {@code "PUBLIC"} or {@code "PRIVATE"}.
     */
    private String visibility;

    /** How long the challenge lasts. */
    private Duration duration;

    /** The user who created this challenge. */
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /** The sport on which this challenge is based. */
    @ManyToOne
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    /**
     * Constructs a fully populated {@code Challenge}.
     *
     * @param title      the display title
     * @param visibility the visibility setting ({@code "PUBLIC"} or {@code "PRIVATE"})
     * @param duration   how long the challenge lasts
     * @param creator    the user who creates the challenge
     * @param sport      the sport targeted by the challenge
     */
    public Challenge(String title, String visibility, Duration duration, User creator, Sport sport) {
        this.title = title;
        this.visibility = visibility;
        this.duration = duration;
        this.creator = creator;
        this.sport = sport;
    }

    /**
     * No-argument constructor required by JPA.
     */
    public Challenge() {
    }

    /**
     * Returns the display title of this challenge.
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the visibility setting of this challenge.
     *
     * @return {@code "PUBLIC"} or {@code "PRIVATE"}
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Returns the duration of this challenge.
     *
     * @return the duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Returns the user who created this challenge.
     *
     * @return the creator {@link User}
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Returns the sport targeted by this challenge.
     *
     * @return the associated {@link Sport}
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * Returns the unique identifier of this challenge.
     *
     * @return the challenge ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the display title of this challenge.
     *
     * @param title the title string
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Sets the visibility setting of this challenge.
     *
     * @param visibility {@code "PUBLIC"} or {@code "PRIVATE"}
     */
    public void setVisibility(String visibility) { this.visibility = visibility; }

    /**
     * Sets the duration of this challenge.
     *
     * @param duration the duration
     */
    public void setDuration(Duration duration) { this.duration = duration; }

    /**
     * Sets the creator of this challenge.
     *
     * @param creator the creator {@link User}
     */
    public void setCreator(User creator) { this.creator = creator; }
}
