package fr.utc.miage.transpitrack.Model;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ChallengeTest {

    @Test
    void constructorShouldCreateChallengeWithAllFields() {
        User creator = new User();
        Challenge challenge = new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), creator, null);

        assertNotNull(challenge);
    }
}
