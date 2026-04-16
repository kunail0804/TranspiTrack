package fr.utc.miage.transpitrack.model.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.jpa.GoalRepository;
import fr.utc.miage.transpitrack.model.jpa.GoalService;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    // ── getAllGoals ─────────────────────────────────────────────────

    @Test
    void getAllGoalsShouldReturnAllGoals() {
        Goal g1 = new Goal();
        Goal g2 = new Goal();
        when(goalRepository.findAll()).thenReturn(List.of(g1, g2));

        List<Goal> result = goalService.getAllGoals();

        assertEquals(2, result.size());
        verify(goalRepository).findAll();
    }

    // ── getGoalByUser ───────────────────────────────────────────────

    @Test
    void getGoalByUserShouldReturnGoalsForUser() {
        User user = new User();
        Goal goal = new Goal(10.0, "Courir 10 km", user);
        when(goalRepository.findByUser(user)).thenReturn(List.of(goal));

        List<Goal> result = goalService.getGoalByUser(user);

        assertEquals(1, result.size());
        verify(goalRepository).findByUser(user);
    }

    // ── createGoal ─────────────────────────────────────────────────

    @Test
    void createGoalShouldSaveAndReturnGoal() {
        Goal goal = new Goal(5.0, "Test", new User());
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal result = goalService.createGoal(goal);

        assertEquals(goal, result);
        verify(goalRepository).save(goal);
    }

    // ── updateGoal ─────────────────────────────────────────────────

    @Test
    void updateGoalShouldCallSave() {
        Goal goal = new Goal(10.0, "Modifier", new User());

        goalService.updateGoal(goal);

        verify(goalRepository).save(goal);
    }

    // ── deleteGoal ─────────────────────────────────────────────────

    @Test
    void deleteGoalShouldCallDelete() {
        Goal goal = new Goal();

        goalService.deleteGoal(goal);

        verify(goalRepository).delete(goal);
    }

    // ── getGoalById ────────────────────────────────────────────────

    @Test
    void getGoalByIdShouldReturnGoalWhenFound() {
        Goal goal = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertEquals(goal, goalService.getGoalById(1L));
    }

    @Test
    void getGoalByIdShouldReturnNullWhenNotFound() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(goalService.getGoalById(99L));
    }
}
