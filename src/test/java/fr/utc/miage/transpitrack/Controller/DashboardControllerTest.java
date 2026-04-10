package fr.utc.miage.transpitrack.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Sport;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private ActivityService activityService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DashboardController dashboardController;

    // ─────────────────────────────────────────────
    // 1. USER NOT CONNECTED
    // ─────────────────────────────────────────────
    @Test
    void dashboardShouldRedirectToLoginWhenUserNotConnected() {

        when(session.getAttribute("userId")).thenReturn(null);

        String view = dashboardController.dashboard(model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Vous devez être connecté !");
    }

    // ─────────────────────────────────────────────
    // 2. USER CONNECTED EMPTY LIST
    // ─────────────────────────────────────────────
    @Test
    void dashboardShouldReturnViewWhenNoActivities() {

        when(session.getAttribute("userId")).thenReturn(1L);
        when(activityService.getActivitiesByUserId(1L)).thenReturn(List.of());

        String view = dashboardController.dashboard(model, session);

        assertEquals("users/dashboard", view);

        verify(activityService).getActivitiesByUserId(1L);
        verify(model).addAttribute("distanceBySport", java.util.Map.of());
        verify(model).addAttribute("durationBySport", java.util.Map.of());
        verify(model).addAttribute("countBySport", java.util.Map.of());
        verify(model).addAttribute("distanceBySportName", java.util.Map.of());
    }

    // ─────────────────────────────────────────────
    // 3. USER CONNECTED WITH ACTIVITIES
    // ─────────────────────────────────────────────
    @Test
    void dashboardShouldComputeStatsCorrectly() {

        when(session.getAttribute("userId")).thenReturn(1L);

        Sport running = new Sport();
        running.setName("Running");

        Activity a1 = new Activity();
        a1.setSport(running);
        a1.setDistance(10.0);
        a1.setDuration(60);

        Activity a2 = new Activity();
        a2.setSport(running);
        a2.setDistance(5.0);
        a2.setDuration(30);

        when(activityService.getActivitiesByUserId(1L))
                .thenReturn(List.of(a1, a2));

        String view = dashboardController.dashboard(model, session);

        assertEquals("users/dashboard", view);

        verify(activityService).getActivitiesByUserId(1L);

        verify(model).addAttribute(eq("distanceBySport"), any());
        verify(model).addAttribute(eq("durationBySport"), any());
        verify(model).addAttribute(eq("countBySport"), any());
        verify(model).addAttribute(eq("distanceBySportName"), any());
    }
}