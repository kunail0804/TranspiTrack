package fr.utc.miage.transpitrack.model.enumer;

/**
 * Represents the emoji reaction a user can attach to a
 * {@link fr.utc.miage.transpitrack.model.Commentary}.
 * Each constant carries its Unicode emoji representation.
 */
public enum ReactionType {

    /** Thumbs-up reaction. */
    LIKE("👍"),

    /** Heart / love reaction. */
    LOVE("❤️"),

    /** Wow / surprised reaction. */
    WOW("😮"),

    /** Thumbs-down / dislike reaction. */
    DISLIKE("👎");

    /** The Unicode emoji string associated with this reaction. */
    private final String emoji;

    /**
     * Constructs a {@code ReactionType} with the given emoji.
     *
     * @param emoji the Unicode emoji string
     */
    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    /**
     * Returns the Unicode emoji string for this reaction.
     *
     * @return the emoji string
     */
    public String getEmoji() {
        return emoji;
    }
}
