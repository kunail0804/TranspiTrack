package fr.utc.miage.transpitrack.model.enumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.enumer.ReactionType;

class ReactionTypeTest {

    @Test
    void likeShouldReturnCorrectEmoji() {
        assertEquals("👍", ReactionType.LIKE.getEmoji());
    }

    @Test
    void loveShouldReturnCorrectEmoji() {
        assertEquals("❤️", ReactionType.LOVE.getEmoji());
    }

    @Test
    void wowShouldReturnCorrectEmoji() {
        assertEquals("😮", ReactionType.WOW.getEmoji());
    }

    @Test
    void dislikeShouldReturnCorrectEmoji() {
        assertEquals("👎", ReactionType.DISLIKE.getEmoji());
    }
}