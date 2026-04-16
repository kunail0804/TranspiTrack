package fr.utc.miage.transpitrack.Model.Jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.function.Executable;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Badge;
import fr.utc.miage.transpitrack.Model.Enum.BadgeType;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserBadge;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    // ── getUserBadges ───────────────────────────────────────────────
    @Test
    void getUserBadgesShouldReturnBadgesForUser() {
        User user = new User();
        UserBadge ub = new UserBadge();
        when(userBadgeRepository.findByUser(user)).thenReturn(List.of(ub));

        List<UserBadge> result = badgeService.getUserBadges(user);

        assertEquals(1, result.size());
        verify(userBadgeRepository).findByUser(user);
    }

    // ── checkAndAwardBadges : DISTANCE ─────────────────────────────
    @Test
    void checkAndAwardBadgesShouldAwardDistanceBadgeWhenThresholdMet() {
        User user = new User();
        user.setWeight(70.0);

        Activity a = new Activity();
        a.setDistance(15.0);
        a.setDuration(60);

        Badge badge = new Badge("Coureur", "100 km", 10.0, BadgeType.DISTANCE);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    void checkAndAwardBadgesShouldNotAwardDistanceBadgeWhenThresholdNotMet() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(5.0);
        a.setDuration(30);

        Badge badge = new Badge("Coureur", "100 km", 100.0, BadgeType.DISTANCE);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository, never()).save(any());
    }

    // ── checkAndAwardBadges : ACTIVITY_COUNT ───────────────────────
    @Test
    void checkAndAwardBadgesShouldAwardActivityCountBadgeWhenThresholdMet() {
        User user = new User();

        Activity a1 = new Activity();
        a1.setDistance(1.0);
        a1.setDuration(10);
        Activity a2 = new Activity();
        a2.setDistance(1.0);
        a2.setDuration(10);

        Badge badge = new Badge("Régulier", "5 activités", 2.0, BadgeType.ACTIVITY_COUNT);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a1, a2));

        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    // ── checkAndAwardBadges : DURATION ─────────────────────────────
    @Test
    void checkAndAwardBadgesShouldAwardDurationBadgeWhenThresholdMet() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(5.0);
        a.setDuration(90);

        Badge badge = new Badge("Première heure", "60 min", 60.0, BadgeType.DURATION);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    // ── checkAndAwardBadges : déjà obtenu ──────────────────────────
    @Test
    void checkAndAwardBadgesShouldNotAwardBadgeAlreadyEarned() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(15.0);
        a.setDuration(90);

        Badge badge = new Badge("Coureur", "100 km", 10.0, BadgeType.DISTANCE);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(true);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository, never()).save(any());
    }

    // ── checkAndAwardBadges : plusieurs badges à la fois ───────────
    @Test
    void checkAndAwardBadgesShouldAwardMultipleBadgesAtOnce() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(10.0);
        a.setDuration(90);

        Badge distanceBadge = new Badge("Premiers km", "10 km", 10.0, BadgeType.DISTANCE);
        Badge durationBadge = new Badge("Première heure", "60 min", 60.0, BadgeType.DURATION);
        Badge countBadge = new Badge("Premier pas", "1 activité", 1.0, BadgeType.ACTIVITY_COUNT);

        when(badgeRepository.findAll()).thenReturn(List.of(distanceBadge, durationBadge, countBadge));
        when(userBadgeRepository.existsByUserAndBadge(any(), any())).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository, times(3)).save(any(UserBadge.class));
    }

    // ── checkAndAwardBadges : liste vide ───────────────────────────
    @Test
    void checkAndAwardBadgesShouldNotAwardAnythingWhenNoActivities() {
        User user = new User();

        Badge badge = new Badge("Coureur", "100 km", 10.0, BadgeType.DISTANCE);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of());

        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    void checkAndAwardBadgesShouldNotAwardActivityCountBadgeWhenThresholdNotMet() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(1.0);
        a.setDuration(10);

        Badge badge = new Badge("Régulier", "5 activités", 5.0, BadgeType.ACTIVITY_COUNT);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    void checkAndAwardBadgesShouldNotAwardDurationBadgeWhenThresholdNotMet() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(1.0);
        a.setDuration(10);

        Badge badge = new Badge("Endurant", "5h", 300.0, BadgeType.DURATION);
        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        badgeService.checkAndAwardBadges(user, List.of(a));

        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    void checkAndAwardBadgesShouldThrowWhenBadgeTypeIsNull() {
        User user = new User();

        Activity a = new Activity();
        a.setDistance(100.0);
        a.setDuration(100);

        Badge badge = new Badge("Invalid", "No type", 0.0, null);

        when(badgeRepository.findAll()).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserAndBadge(user, badge)).thenReturn(false);

        List<Activity> activities = List.of(a);

        Executable exec = () -> badgeService.checkAndAwardBadges(user, activities);

        IllegalStateException ex = assertThrows(IllegalStateException.class, exec);

        assertEquals("Badge type cannot be null", ex.getMessage());

        verify(userBadgeRepository, never()).save(any());
    }
}
