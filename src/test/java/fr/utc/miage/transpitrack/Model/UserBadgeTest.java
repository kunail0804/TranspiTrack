package fr.utc.miage.transpitrack.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.Model.Enum.BadgeType;

class UserBadgeTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        User user = new User();
        Badge badge = new Badge("Coureur", "100 km", 100.0, BadgeType.DISTANCE);
        LocalDate date = LocalDate.of(2024, 6, 1);

        UserBadge ub = new UserBadge(user, badge, date);

        assertEquals(user, ub.getUser());
        assertEquals(badge, ub.getBadge());
        assertEquals(date, ub.getEarnedAt());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        UserBadge ub = new UserBadge();

        assertNull(ub.getUser());
        assertNull(ub.getBadge());
        assertNull(ub.getEarnedAt());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new UserBadge().getId());
    }

    // ── Setters ────────────────────────────────────────────────────

    @Test
    void settersShouldUpdateFields() {
        UserBadge ub = new UserBadge();
        User user = new User();
        Badge badge = new Badge("Assidu", "50 activités", 50.0, BadgeType.ACTIVITY_COUNT);
        LocalDate date = LocalDate.of(2025, 1, 15);

        ub.setUser(user);
        ub.setBadge(badge);
        ub.setEarnedAt(date);

        assertEquals(user, ub.getUser());
        assertEquals(badge, ub.getBadge());
        assertEquals(date, ub.getEarnedAt());
    }
}
