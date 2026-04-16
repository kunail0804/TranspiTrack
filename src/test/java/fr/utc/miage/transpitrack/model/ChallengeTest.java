package fr.utc.miage.transpitrack.model;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;


class ChallengeTest {

    @Test
    void constructorShouldCreateChallengeWithAllFields() {
        User creator = new User();
        Sport sport = new Sport();
        Duration duration = Duration.ofDays(7);

        Challenge challenge = new Challenge("Run 5km", "PUBLIC", duration, creator, sport);

        assertNotNull(challenge);
        assertEquals("Run 5km", challenge.getTitle());
        assertEquals("PUBLIC", challenge.getVisibility());
        assertEquals(duration, challenge.getDuration());
        assertEquals(creator, challenge.getCreator());
        assertEquals(sport, challenge.getSport());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        Challenge challenge = new Challenge();

        assertNull(challenge.getTitle());
        assertNull(challenge.getVisibility());
        assertNull(challenge.getDuration());
        assertNull(challenge.getCreator());
        assertNull(challenge.getSport());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new Challenge().getId());
    }

    @Test
    void settersShouldUpdateFields() {
        Challenge challenge = new Challenge();
        User user = new User();
        Duration duration = Duration.ofDays(14);

        challenge.setTitle("Nouveau titre");
        challenge.setVisibility("PRIVATE");
        challenge.setDuration(duration);
        challenge.setCreator(user);

        assertEquals("Nouveau titre", challenge.getTitle());
        assertEquals("PRIVATE", challenge.getVisibility());
        assertEquals(duration, challenge.getDuration());
        assertEquals(user, challenge.getCreator());
    }

}
