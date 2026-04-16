package fr.utc.miage.transpitrack.controller;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.Temporality;
import fr.utc.miage.transpitrack.model.jpa.GoalService;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Mock
    private SportService sportService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;

    @InjectMocks
    private GoalController goalController;

    @Test
    void consultationGoalsShouldRedirectToLoginWhenNotLoggedIn() {
        String view = goalController.consultationGoals(model, session);

        assertEquals("redirect:/users/formLogin", view);
        verify(model).addAttribute("message", "Il faut être connecte !");
    }

    @Test
    void consultationGoalsShouldReturnGoalsViewWhenLoggedIn() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = goalController.consultationGoals(model, session);

        assertEquals("goals/listGoals", view);
        verify(model).addAttribute("goals", user.getGoals());
    }

    @Test
    void addGoalShouldRedirectToLoginWhenNotLoggedIn() {
        String view = goalController.addGoal("Courir", 10.0, 1L, Temporality.QUOTIDIEN, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @ParameterizedTest
    @MethodSource("provideNullAddGoalParams")
    void addGoalShouldRedirectWhenRequiredParamIsNull(String text, Double distance) {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = goalController.addGoal(text, distance, 1L, Temporality.QUOTIDIEN, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    static Stream<Arguments> provideNullAddGoalParams() {
        return Stream.of(
            Arguments.of(null, 10.0),
            Arguments.of("Courir", null)
        );
    }

    @Test
    void addGoalShouldSaveAndRedirectWhenValid() {
        User user = new User();
        Sport sport = new Sport();

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getSportById(1L)).thenReturn(sport);

        String view = goalController.addGoal("Courir 10 km", 10.0, 1L, Temporality.HEBDOMADAIRE, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).createGoal(any(Goal.class));
        verify(userService).updateUser(user);
    }

    @Test
    void updateGoalShouldRedirectToLoginWhenNotLoggedIn() {
        String view = goalController.updateGoal(1L, "Courir", 10.0, 1L, Temporality.QUOTIDIEN, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @ParameterizedTest
    @MethodSource("provideNullUpdateGoalParams")
    void updateGoalShouldRedirectWhenRequiredParamIsNull(String text, Double distance) {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = goalController.updateGoal(1L, text, distance, 1L, Temporality.QUOTIDIEN, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    static Stream<Arguments> provideNullUpdateGoalParams() {
        return Stream.of(
            Arguments.of(null, 10.0),
            Arguments.of("Courir", null)
        );
    }

    @Test
    void updateGoalShouldUpdateAndRedirectWhenValid() {
        User user = new User();
        Sport oldSport = new Sport();
        Sport newSport = new Sport();
        Goal goal = new Goal(5.0, "Ancienne", user, oldSport, Temporality.QUOTIDIEN);

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(goalService.getGoalById(1L)).thenReturn(goal);
        when(sportService.getSportById(2L)).thenReturn(newSport);

        String view = goalController.updateGoal(1L, "Courir 10 km", 10.0, 2L, Temporality.MENSUEL, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).updateGoal(goal);
        verify(userService).updateUser(user);
        assertEquals("Courir 10 km", goal.getGoalText());
        assertEquals(10.0, goal.getTargetDistance(), 0.001);
        assertEquals(newSport, goal.getSport());
        assertEquals(Temporality.MENSUEL, goal.getTemporality());
    }

    @Test
    void deleteGoalShouldRedirectToLoginWhenNotLoggedIn() {
        String view = goalController.deleteGoal(1L, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void deleteGoalShouldRedirectToGoalsWhenGoalIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = goalController.deleteGoal(null, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    @Test
    void deleteGoalShouldDeleteAndRedirectWhenValid() {
        User user = new User();
        Goal goal = new Goal(10.0, "Courir", user);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(goalService.getGoalById(1L)).thenReturn(goal);

        String view = goalController.deleteGoal(1L, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).deleteGoal(goal);
        verify(userService).updateUser(user);
    }
}
