package fr.utc.miage.transpitrack.Model;

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

}
