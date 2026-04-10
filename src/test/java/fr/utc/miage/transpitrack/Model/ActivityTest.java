package fr.utc.miage.transpitrack.Model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ActivityTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        Sport sport = new Sport();
        User user = new User();
        LocalDate date = LocalDate.of(2024, 1, 1);

        Activity activity = new Activity(date, 30, 5.0, "good", sport, user);

        assertEquals(date, activity.getDate());
        assertEquals(30, activity.getDuration());
        assertEquals(5.0, activity.getDistance());
        assertEquals("good", activity.getEvaluation());
        assertEquals(sport, activity.getSport());
        assertEquals(user, activity.getUser());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new Activity().getId());
    }

    // ── Setters ────────────────────────────────────────────────────

    @Test
    void settersShouldUpdateFields() {
        Activity activity = new Activity();
        Sport sport = new Sport();
        User user = new User();
        LocalDate date = LocalDate.of(2025, 6, 15);

        activity.setDate(date);
        activity.setDuration(60);
        activity.setDistance(10.5);
        activity.setEvaluation("great");
        activity.setSport(sport);
        activity.setUser(user);

        assertEquals(date, activity.getDate());
        assertEquals(60, activity.getDuration());
        assertEquals(10.5, activity.getDistance());
        assertEquals("great", activity.getEvaluation());
        assertEquals(sport, activity.getSport());
        assertEquals(user, activity.getUser());
    }

    // ── Logique métier ─────────────────────────────────────────────

    @Test
    void getTotalCaloriesShouldReturnOne() {
        assertEquals(1, new Activity().getTotalCalories());
    }
}
