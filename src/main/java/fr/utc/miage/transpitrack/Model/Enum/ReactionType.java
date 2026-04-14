package fr.utc.miage.transpitrack.Model.Enum;

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