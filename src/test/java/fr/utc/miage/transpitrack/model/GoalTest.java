package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.User;

class GoalTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        User user = new User();
        Goal goal = new Goal(10.0, "Courir 10 km", user);

        assertEquals(10.0, goal.getTargetDistance(), 0.001);
        assertEquals("Courir 10 km", goal.getGoalText());
        assertEquals(user, goal.getUser());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        Goal goal = new Goal();

        assertNull(goal.getTargetDistance());
        assertNull(goal.getGoalText());
        assertNull(goal.getUser());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new Goal().getId());
    }

    // ── Setters ────────────────────────────────────────────────────

    @Test
    void settersShouldUpdateFields() {
        Goal goal = new Goal();
        User user = new User();

        goal.setTargetDistance(42.195);
        goal.setGoalText("Finir un marathon");
        goal.setUser(user);

        assertEquals(42.195, goal.getTargetDistance(), 0.001);
        assertEquals("Finir un marathon", goal.getGoalText());
        assertEquals(user, goal.getUser());
    }
}
