package fr.utc.miage.transpitrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity recording the score submitted by a user for a specific {@link Challenge}.
 * <p>
 * Each {@code ChallengeScore} links a {@link User} to a {@link Challenge} with a
 * numeric score. A user may update their score, but only one score entry per
 * user/challenge pair exists in the database.
 * </p>
 */
@Entity
@Table(name = "challenge_score")
public class ChallengeScore {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "challenge_score_seq")
    private Long id;

    /** The user who submitted this score. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The challenge to which this score belongs. */
    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    /** The numeric score value submitted by the user. */
    private double score;

    /**
     * No-argument constructor required by JPA.
     */
    public ChallengeScore() {}

    /**
     * Constructs a {@code ChallengeScore} with all fields.
     *
     * @param user      the user submitting the score
     * @param challenge the challenge being scored
     * @param score     the numeric score value
     */
    public ChallengeScore(User user, Challenge challenge, double score) {
        this.user = user;
        this.challenge = challenge;
        this.score = score;
    }

    /**
     * Returns the unique identifier of this score entry.
     *
     * @return the score ID
     */
    public Long getId() { return id; }

    /**
     * Returns the user who submitted this score.
     *
     * @return the associated {@link User}
     */
    public User getUser() { return user; }

    /**
     * Returns the challenge to which this score belongs.
     *
     * @return the associated {@link Challenge}
     */
    public Challenge getChallenge() { return challenge; }

    /**
     * Returns the numeric score value.
     *
     * @return the score
     */
    public double getScore() { return score; }

    /**
     * Updates the numeric score value.
     *
     * @param score the new score
     */
    public void setScore(double score) { this.score = score; }
}
