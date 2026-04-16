package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.BadgeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * JPA entity representing a gamification badge that can be earned by users.
 * <p>
 * A badge has a type ({@link BadgeType}) that determines which metric is measured
 * (distance, activity count, or duration), and a threshold value that the user must
 * reach or exceed to earn the badge.
 * An optional image URL is used for badges that have a visual icon.
 * </p>
 */
@Entity
public class Badge {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Short display name of the badge (e.g., "Coureur"). */
    private String title;

    /** Human-readable description of the badge's unlocking condition. */
    private String description;

    /**
     * Numeric threshold the user must meet or exceed to earn this badge.
     * The unit depends on the {@link #badgeType} (km, count, or minutes).
     */
    private double thresholdValue;

    /** The metric type used to evaluate whether this badge has been earned. */
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    /** Filename of the badge's icon image, or {@code null} if no image is assigned. */
    private String urlImage;

    /**
     * No-argument constructor required by JPA.
     */
    public Badge() {}

    /**
     * Constructs a {@code Badge} without an image.
     *
     * @param title          the short display name
     * @param description    the unlocking condition description
     * @param thresholdValue the threshold value to reach
     * @param badgeType      the metric type used for evaluation
     */
    public Badge(String title, String description, double thresholdValue, BadgeType badgeType) {
        this.title = title;
        this.description = description;
        this.thresholdValue = thresholdValue;
        this.badgeType = badgeType;
    }

    /**
     * Constructs a {@code Badge} with an optional image filename.
     *
     * @param title          the short display name
     * @param description    the unlocking condition description
     * @param thresholdValue the threshold value to reach
     * @param badgeType      the metric type used for evaluation
     * @param url            the image filename, or {@code null}
     */
    public Badge(String title, String description, double thresholdValue, BadgeType badgeType, String url) {
        this.title = title;
        this.description = description;
        this.thresholdValue = thresholdValue;
        this.badgeType = badgeType;
        this.urlImage = url;
    }

    /**
     * Returns the unique identifier of this badge.
     *
     * @return the badge ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the display title of this badge.
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of this badge's unlocking condition.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the threshold value that must be reached to earn this badge.
     *
     * @return the threshold value (unit depends on {@link #getBadgeType()})
     */
    public double getThresholdValue() {
        return thresholdValue;
    }

    /**
     * Returns the metric type used to evaluate this badge.
     *
     * @return the {@link BadgeType}
     */
    public BadgeType getBadgeType() {
        return badgeType;
    }

    /**
     * Sets the display title of this badge.
     *
     * @param title the title string
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description of this badge's unlocking condition.
     *
     * @param description the description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the threshold value for this badge.
     *
     * @param thresholdValue the threshold value
     */
    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    /**
     * Sets the metric type used to evaluate this badge.
     *
     * @param badgeType the {@link BadgeType}
     */
    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

    /**
     * Returns the filename of this badge's icon image.
     *
     * @return the image filename, or {@code null} if no image is assigned
     */
    public String getUrlImage() {
        return urlImage;
    }

    /**
     * Sets the filename of this badge's icon image.
     *
     * @param urlImage the image filename, or {@code null} to clear it
     */
    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
