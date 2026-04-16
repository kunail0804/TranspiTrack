package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.enumer.ReactionType;

import java.lang.reflect.Field;

class CommentaryTest {

    // ─────────────────────────────────────────────
    // constructeur vide + setters
    // ─────────────────────────────────────────────
    @Test
    void shouldSetAndGetFieldsCorrectly() {

        Commentary commentary = new Commentary();

        User author = new User();
        Activity activity = new Activity();

        commentary.setMessage("Hello world");
        commentary.setReaction(ReactionType.LOVE);
        commentary.setAuthor(author);
        commentary.setActivity(activity);

        assertEquals("Hello world", commentary.getMessage());
        assertEquals(ReactionType.LOVE, commentary.getReaction());
        assertEquals(author, commentary.getAuthor());
        assertEquals(activity, commentary.getActivity());
    }

    // ─────────────────────────────────────────────
    // constructeur complet
    // ─────────────────────────────────────────────
    @Test
    void constructorShouldInitializeFields() {

        User author = new User();
        Activity activity = new Activity();

        Commentary commentary = new Commentary(
                "Nice activity",
                ReactionType.WOW,
                author,
                activity
        );

        assertEquals("Nice activity", commentary.getMessage());
        assertEquals(ReactionType.WOW, commentary.getReaction());
        assertEquals(author, commentary.getAuthor());
        assertEquals(activity, commentary.getActivity());
    }

    // ─────────────────────────────────────────────
    // null values (important car champs nullable)
    // ─────────────────────────────────────────────
    @Test
    void shouldAllowNullValues() {

        Commentary commentary = new Commentary();

        commentary.setMessage(null);
        commentary.setReaction(null);

        assertEquals(null, commentary.getMessage());
        assertEquals(null, commentary.getReaction());
    }

    // ─────────────────────────────────────────────
    // reaction update
    // ─────────────────────────────────────────────
    @Test
    void shouldUpdateReaction() {

        Commentary commentary = new Commentary();

        commentary.setReaction(ReactionType.LIKE);
        assertEquals(ReactionType.LIKE, commentary.getReaction());

        commentary.setReaction(ReactionType.DISLIKE);
        assertEquals(ReactionType.DISLIKE, commentary.getReaction());
    }

    // ─────────────────────────────────────────────
    // getId()
    // ─────────────────────────────────────────────

    @Test
    void getIdShouldReturnValueSetByReflection() throws Exception {

        Commentary commentary = new Commentary();

        Field field = Commentary.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(commentary, 42L);

        assertEquals(42L, commentary.getId());
    }

}