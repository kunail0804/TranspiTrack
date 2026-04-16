package fr.utc.miage.transpitrack.model.enumer;

/**
 * Defines the criterion used to evaluate whether a {@link fr.utc.miage.transpitrack.model.Badge}
 * has been earned by a user.
 */
public enum BadgeType {

    /** Badge earned based on the total distance covered across all activities. */
    DISTANCE,

    /** Badge earned based on the total number of activities completed. */
    ACTIVITY_COUNT,

    /** Badge earned based on the cumulated training duration in minutes. */
    DURATION
}
