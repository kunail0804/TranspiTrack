package fr.utc.miage.transpitrack.Controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @Mock
    private UserService userService;

    @Mock
    private SportService sportService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ActivityController activityController;

    // ──────────────────────────────────────────────────────────────
    // GET /activities
    // ──────────────────────────────────────────────────────────────

    @Test
    void listActivitiesShouldReturnListViewWithActivitiesSortedByDateDesc() {
        Activity older = new Activity();
        older.setDate(LocalDate.of(2024, 1, 1));
        Activity newer = new Activity();
        newer.setDate(LocalDate.of(2024, 6, 1));
        when(activityService.getAllActivities()).thenReturn(Arrays.asList(older, newer));

        String view = activityController.listActivities(model);

        assertEquals("activities/list", view);
        verify(model).addAttribute(any(String.class), any());
    }

    // ──────────────────────────────────────────────────────────────
    // GET /activities/add
    // ──────────────────────────────────────────────────────────────

    @Test
    void addActivityShouldReturnAddViewWithoutError() {
        String view = activityController.addActivity(null, model, session);

        assertEquals("activities/add", view);
        verify(model, times(2)).addAttribute(any(String.class), any());
    }

    @Test
    void addActivityShouldAddErrorToModelWhenErrorParamPresent() {
        String view = activityController.addActivity("invalid_duration", model, session);

        assertEquals("activities/add", view);
        verify(model).addAttribute("error", "invalid_duration");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /activities/add
    // ──────────────────────────────────────────────────────────────

    @Test
    void saveActivityShouldRedirectWithErrorWhenDurationIsZero() {
        Activity activity = new Activity();
        activity.setDuration(0);

        String view = activityController.saveActivity(activity, 1L, session);

        assertEquals("redirect:/activities/add?error=invalid_duration", view);
    }

    @Test
    void saveActivityShouldRedirectWithErrorWhenDurationIsNegative() {
        Activity activity = new Activity();
        activity.setDuration(-5);

        String view = activityController.saveActivity(activity, 1L, session);

        assertEquals("redirect:/activities/add?error=invalid_duration", view);
    }

    @Test
    void saveActivityShouldRedirectWithErrorWhenDateIsNull() {
        Activity activity = new Activity();
        activity.setDuration(30);
        activity.setDate(null);

        String view = activityController.saveActivity(activity, 1L, session);

        assertEquals("redirect:/activities/add?error=invalid_date", view);
    }

    @Test
    void saveActivityShouldRedirectWithErrorWhenDistanceIsNegative() {
        Activity activity = new Activity();
        activity.setDuration(30);
        activity.setDate(LocalDate.now());
        activity.setDistance(-1.0);

        String view = activityController.saveActivity(activity, 1L, session);

        assertEquals("redirect:/activities/add?error=invalid_distance", view);
    }

    @Test
    void saveActivityShouldSaveAndRedirectWhenValid() {
        Activity activity = new Activity();
        activity.setDuration(30);
        activity.setDate(LocalDate.now());
        activity.setDistance(5.0);

        when(sportService.getSportById(1L)).thenReturn(new Sport());
        when(session.getAttribute("userId")).thenReturn(2L);
        when(userService.getUserById(2L)).thenReturn(new User());

        String view = activityController.saveActivity(activity, 1L, session);

        assertEquals("redirect:/activities", view);
        verify(activityService).save(activity);
    }
}
