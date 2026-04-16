package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.enumer.BadgeType;

class BadgeTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        Badge badge = new Badge("Coureur", "Parcours 100 km", 100.0, BadgeType.DISTANCE);

        assertEquals("Coureur", badge.getTitle());
        assertEquals("Parcours 100 km", badge.getDescription());
        assertEquals(100.0, badge.getThresholdValue(), 0.001);
        assertEquals(BadgeType.DISTANCE, badge.getBadgeType());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        Badge badge = new Badge();

        assertNull(badge.getTitle());
        assertNull(badge.getDescription());
        assertNull(badge.getBadgeType());
        assertEquals(0.0, badge.getThresholdValue(), 0.001);
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new Badge().getId());
    }

    // ── Setters ────────────────────────────────────────────────────

    @Test
    void settersShouldUpdateFields() {
        Badge badge = new Badge();

        badge.setTitle("Ultra");
        badge.setDescription("Parcours 1000 km");
        badge.setThresholdValue(1000.0);
        badge.setBadgeType(BadgeType.DISTANCE);

        assertEquals("Ultra", badge.getTitle());
        assertEquals("Parcours 1000 km", badge.getDescription());
        assertEquals(1000.0, badge.getThresholdValue(), 0.001);
        assertEquals(BadgeType.DISTANCE, badge.getBadgeType());
    }

    @Test
    void constructorShouldSupportAllBadgeTypes() {
        for (BadgeType type : BadgeType.values()) {
            Badge badge = new Badge("t", "d", 1.0, type);
            assertEquals(type, badge.getBadgeType());
        }
    }
}
