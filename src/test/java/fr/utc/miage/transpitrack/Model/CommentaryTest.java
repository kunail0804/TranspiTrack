package fr.utc.miage.transpitrack.Model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class CommentaryTest {

    @Test
    void constructorShouldCreateCommentary() {
        User user = new User();
        Activity activity = new Activity();

        Commentary commentary = new Commentary("Super activité !", user, activity);

        assertNotNull(commentary);
    }
}
