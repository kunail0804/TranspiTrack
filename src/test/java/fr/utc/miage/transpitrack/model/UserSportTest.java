package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.enumer.Level;

class UserSportTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        User user = new User();
        Sport sport = new Sport();

        UserSport us = new UserSport(user, sport, Level.BEGINNER);

        assertEquals(user, us.getUser());
        assertEquals(sport, us.getSport());
        assertEquals(Level.BEGINNER, us.getLevel());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        UserSport us = new UserSport();

        assertNull(us.getUser());
        assertNull(us.getSport());
        assertNull(us.getLevel());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new UserSport().getId());
    }

    // ── Setter ─────────────────────────────────────────────────────

    @Test
    void setLevelShouldUpdateLevel() {
        UserSport us = new UserSport();

        us.setLevel(Level.INTERMEDIATE);

        assertEquals(Level.INTERMEDIATE, us.getLevel());
    }

    @Test
    void setLevelShouldSupportAllLevelValues() {
        UserSport us = new UserSport();

        for (Level level : Level.values()) {
            us.setLevel(level);
            assertEquals(level, us.getLevel());
        }
    }
}
