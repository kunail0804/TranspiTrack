package fr.utc.miage.transpitrack.model.enumer;

public enum ReactionType {
    LIKE("👍"),
    LOVE("❤️"),
    WOW("😮"),
    DISLIKE("👎");


    private final String emoji;

    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}