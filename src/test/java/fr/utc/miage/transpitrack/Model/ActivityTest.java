package fr.utc.miage.transpitrack.Model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActivityTest {

    private Activity buildActivity() {
        Sport sport = new Sport();
        User user = new User();
        return new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", sport, user);
    }

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

    // ── equals ─────────────────────────────────────────────────────

    @Test
    void equalsShouldReturnTrueWhenSameInstance() {
        Activity activity = buildActivity();
        assertEquals(activity, activity);
    }

    @Test
    void equalsShouldReturnFalseWhenNull() {
        assertNotEquals(null, buildActivity());
    }

    @Test
    void equalsShouldReturnFalseWhenDifferentClass() {
        assertNotEquals("not an activity", buildActivity());
    }

    @Test
    void equalsShouldReturnFalseWhenDistanceDiffers() {
        Activity a1 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", new Sport(), new User());
        Activity a2 = new Activity(LocalDate.of(2024, 1, 1), 30, 9.0, "good", new Sport(), new User());
        assertNotEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnFalseWhenEvaluationDiffers() {
        Activity a1 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", new Sport(), new User());
        Activity a2 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "bad", new Sport(), new User());
        assertNotEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnFalseWhenDateDiffers() {
        Activity a1 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", new Sport(), new User());
        Activity a2 = new Activity(LocalDate.of(2025, 1, 1), 30, 5.0, "good", new Sport(), new User());
        assertNotEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnFalseWhenDurationDiffers() {
        Activity a1 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", new Sport(), new User());
        Activity a2 = new Activity(LocalDate.of(2024, 1, 1), 60, 5.0, "good", new Sport(), new User());
        assertNotEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnTrueWhenAllFieldsEqual() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Sport sport = new Sport();
        User user = new User();
        Activity a1 = new Activity(date, 30, 5.0, "good", sport, user);
        Activity a2 = new Activity(date, 30, 5.0, "good", sport, user);
        assertEquals(a1, a2);
    }

    // ── hashCode ───────────────────────────────────────────────────

    @Test
    void hashCodeShouldBeEqualForEqualObjects() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Sport sport = new Sport();
        User user = new User();
        Activity a1 = new Activity(date, 30, 5.0, "good", sport, user);
        Activity a2 = new Activity(date, 30, 5.0, "good", sport, user);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void hashCodeShouldDifferForDifferentObjects() {
        Activity a1 = new Activity(LocalDate.of(2024, 1, 1), 30, 5.0, "good", new Sport(), new User());
        Activity a2 = new Activity(LocalDate.of(2025, 6, 1), 60, 10.0, "bad", new Sport(), new User());
        assertNotEquals(a1.hashCode(), a2.hashCode());
    }

    // ── toString ───────────────────────────────────────────────────

    @Test
    void toStringShouldContainFieldValues() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Activity activity = new Activity(date, 30, 5.0, "good", new Sport(), new User());

        String result = activity.toString();

        assertTrue(result.contains("Activity{"));
        assertTrue(result.contains("2024-01-01"));
        assertTrue(result.contains("30"));
        assertTrue(result.contains("5.0"));
        assertTrue(result.contains("good"));
    }
}
