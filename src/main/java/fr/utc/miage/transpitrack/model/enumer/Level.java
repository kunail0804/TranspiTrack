package fr.utc.miage.transpitrack.model.enumer;

/**
 * Represents a user's self-assessed proficiency level for a given sport,
 * stored in a {@link fr.utc.miage.transpitrack.model.UserSport} preference.
 */
public enum Level {

    /** Absolute beginner with little or no experience. */
    BEGINNER,

    /** Casual practitioner with basic knowledge. */
    AMATEUR,

    /** Regular practitioner with solid foundations. */
    INTERMEDIATE,

    /** Experienced practitioner with high technical skills. */
    ADVANCED,

    /** Elite-level practitioner or competitive athlete. */
    PROFESSIONAL
}
