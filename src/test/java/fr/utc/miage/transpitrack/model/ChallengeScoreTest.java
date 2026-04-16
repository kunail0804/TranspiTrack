package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ChallengeScoreTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        User user = new User();
        Challenge challenge = new Challenge();

        ChallengeScore cs = new ChallengeScore(user, challenge, 42.5);

        assertEquals(user, cs.getUser());
        assertEquals(challenge, cs.getChallenge());
        assertEquals(42.5, cs.getScore());
    }

    @Test
    void defaultConstructorShouldLeaveUserAndChallengeNull() {
        ChallengeScore cs = new ChallengeScore();

        assertNull(cs.getUser());
        assertNull(cs.getChallenge());
    }

    @Test
    void defaultConstructorShouldLeaveScoreAtZero() {
        assertEquals(0.0, new ChallengeScore().getScore());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new ChallengeScore().getId());
    }

    // ── Setter ─────────────────────────────────────────────────────

    @Test
    void setScoreShouldUpdateScore() {
        ChallengeScore cs = new ChallengeScore();

        cs.setScore(100.0);

        assertEquals(100.0, cs.getScore());
    }

    @Test
    void setScoreShouldAcceptNegativeScore() {
        ChallengeScore cs = new ChallengeScore();

        cs.setScore(-5.0);

        assertEquals(-5.0, cs.getScore());
    }

    @Test
    void setScoreShouldAcceptZero() {
        ChallengeScore cs = new ChallengeScore(new User(), new Challenge(), 99.0);

        cs.setScore(0.0);

        assertEquals(0.0, cs.getScore());
    }
}
