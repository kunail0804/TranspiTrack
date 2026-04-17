package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.ReactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * JPA entity representing a commentary left by a user on an {@link Activity}.
 * <p>
 * A commentary contains an optional text message and an optional emoji reaction
 * ({@link ReactionType}). The database enforces a unique constraint ensuring that
 * each user can only comment once per activity.
 * </p>
 */
@Entity
@Table(name="commentary", uniqueConstraints = @UniqueConstraint(columnNames = {"author_id", "activity_id"}))
public class Commentary {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentary_seq")
    private Long id;

    /** Optional text message left by the author (may be {@code null}). */
    private String message;

    /** Optional emoji reaction attached to the commentary (may be {@code null}). */
    @Enumerated(EnumType.STRING)
    private ReactionType reaction;

    /** The user who wrote this commentary. */
    @ManyToOne
    private User author;

    /** The activity being commented on. */
    @ManyToOne
    private Activity activity;

    /**
     * No-argument constructor required by JPA.
     */
    public Commentary() {
    }

    /**
     * Constructs a fully populated {@code Commentary}.
     *
     * @param message  the text message, or {@code null}
     * @param reaction the emoji reaction, or {@code null}
     * @param author   the user who writes the commentary
     * @param activity the activity being commented on
     */
    public Commentary(String message, ReactionType reaction, User author, Activity activity) {
        this.message = message;
        this.reaction = reaction;
        this.author = author;
        this.activity = activity;
    }

    /**
     * Returns the unique identifier of this commentary.
     *
     * @return the commentary ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the text message of this commentary.
     *
     * @return the message, or {@code null} if not set
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the emoji reaction attached to this commentary.
     *
     * @return the {@link ReactionType}, or {@code null} if not set
     */
    public ReactionType getReaction() {
        return reaction;
    }

    /**
     * Sets the emoji reaction for this commentary.
     *
     * @param reaction the {@link ReactionType}, or {@code null} to clear it
     */
    public void setReaction(ReactionType reaction) {
        this.reaction = reaction;
    }

    /**
     * Returns the user who wrote this commentary.
     *
     * @return the author {@link User}
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Returns the activity this commentary is attached to.
     *
     * @return the associated {@link Activity}
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the author of this commentary.
     *
     * @param author the author {@link User}
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Sets the activity this commentary is attached to.
     *
     * @param activity the associated {@link Activity}
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sets the text message of this commentary.
     *
     * @param message the message text, or {@code null}
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
