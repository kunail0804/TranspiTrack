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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Commentary;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.BadgeService;
import fr.utc.miage.transpitrack.Model.Jpa.CommentaryService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Jpa.WeatherService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Enum.ReactionType;
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

    @Mock
    private CommentaryService commentaryService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private BadgeService badgeService;
    
    // ──────────────────────────────────────────────────────────────
    // GET /activities
    // ──────────────────────────────────────────────────────────────
    @Test
    @SuppressWarnings("unchecked")
    void listActivitiesShouldReturnListViewWithActivitiesSortedByDateDesc() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        Activity older = new Activity();
        older.setDate(LocalDate.of(2024, 1, 1));
        Activity newer = new Activity();
        newer.setDate(LocalDate.of(2024, 6, 1));
        when(activityService.getActivitiesByUserId(userId)).thenReturn(Arrays.asList(older, newer));

        String view = activityController.listActivities(model, session);

        assertEquals("activities/list", view);
        ArgumentCaptor<List<Activity>> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("activities"), captor.capture());
        List<Activity> sorted = captor.getValue();
        assertEquals(newer, sorted.get(0));
        assertEquals(older, sorted.get(1));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /activities/add
    // ──────────────────────────────────────────────────────────────
    @Test
    void addActivityShouldReturnAddViewWithoutError() {
        String view = activityController.addActivity(null, model, session);

        assertEquals("activities/add", view);
        verify(model).addAttribute(eq("activity"), any(Activity.class));
        verify(model).addAttribute(eq("sports"), any());
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
    void listActivitiesShouldReturnListViewWithEmptyList() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(activityService.getActivitiesByUserId(userId)).thenReturn(new java.util.ArrayList<>());

        String view = activityController.listActivities(model, session);

        assertEquals("activities/list", view);
        verify(model).addAttribute(eq("activities"), any());
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

        assertEquals("redirect:/users/dashboard", view);
        verify(activityService).save(activity);
    }

    @Test
    void getActivityDetailsShouldReturnDetailsViewWhenUserLoggedIn() {

        Long userId = 1L;
        Long activityId = 10L;

        User currentUser = mock(User.class);
        Activity activity = mock(Activity.class);
        User author = mock(User.class);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(currentUser);

        when(activityService.getActivityById(activityId)).thenReturn(activity);
        when(activity.getUser()).thenReturn(author);

        when(commentaryService.getCommentariesByActivityId(activityId)).thenReturn(List.of());

        String view = activityController.getActivityDetails(activityId, model, session);

        assertEquals("activities/details", view);

        verify(model).addAttribute("activity", activity);
        verify(model).addAttribute("user", currentUser);
        verify(model).addAttribute("author", author);
    }

    @Test
    void getActivityDetailsShouldRedirectWhenUserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = activityController.getActivityDetails(10L, model, session);

        assertEquals("redirect:/users/login?msg=Vous devez etre connecte", view);
    }

    @Test
    void getActivityDetailsShouldRedirectWhenUserNotFound() {

        Long userId = 1L;

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(null);

        String view = activityController.getActivityDetails(10L, model, session);

        assertEquals("redirect:/users/login?msg=Utilisateur introuvable", view);
    }

    @Test
    void getActivityDetailsShouldRedirectWhenActivityNotFound() {

        Long userId = 1L;

        User user = mock(User.class);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(user);

        when(activityService.getActivityById(10L)).thenReturn(null);

        String view = activityController.getActivityDetails(10L, model, session);

        assertEquals("redirect:/activities", view);
    }

    @Test
    void addCommentaryShouldSaveAndRedirectWhenValid() {

        Long userId = 1L;
        Long activityId = 10L;

        User user = mock(User.class);
        Activity activity = mock(Activity.class);
        Commentary commentary = new Commentary();

        when(session.getAttribute("userId")).thenReturn(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(activityService.getActivityById(activityId)).thenReturn(activity);

        when(commentaryService.getCommentariesByAuthorIdAndActivityId(userId, activityId))
                .thenReturn(List.of());

        when(activity.getId()).thenReturn(activityId);

        String view = activityController.addCommentary(commentary, activityId, session);

        assertEquals("redirect:/activities/details/10", view);

        verify(commentaryService).createCommentary(commentary);
    }

    @Test
    void addCommentaryShouldRedirectWhenUserNotLoggedIn() {

        when(session.getAttribute("userId")).thenReturn(null);

        String view = activityController.addCommentary(new Commentary(), 10L, session);

        assertEquals(
                "redirect:/users/login?msg=Vous devez etre connecte pour commenter",
                view
        );
    }

    @Test
    void addCommentaryShouldRedirectWhenAlreadyCommented() {

        Long userId = 1L;
        Long activityId = 10L;

        when(session.getAttribute("userId")).thenReturn(userId);

        when(commentaryService.getCommentariesByAuthorIdAndActivityId(userId, activityId))
                .thenReturn(List.of(mock(Commentary.class)));

        String view = activityController.addCommentary(new Commentary(), activityId, session);

        assertEquals(
                "redirect:/activities/details/10?msg=Vous avez deja commente cette activite",
                view
        );
    }

    @Test
    void addCommentaryShouldRedirectWhenActivityNotFound() {

        Long userId = 1L;
        Long activityId = 10L;

        User user = mock(User.class);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentaryService.getCommentariesByAuthorIdAndActivityId(userId, activityId))
                .thenReturn(List.of());

        when(userService.getUserById(userId)).thenReturn(user);
        when(activityService.getActivityById(activityId)).thenReturn(null);

        String view = activityController.addCommentary(new Commentary(), activityId, session);

        assertEquals(
                "redirect:/activities?msg=Activite non trouvee",
                view
        );
    }

    @Test
    void updateReactionShouldUpdateWhenAuthor() {

        Long userId = 1L;
        Long commentId = 5L;
        Long activityId = 10L;

        User user = mock(User.class);
        Activity activity = mock(Activity.class);
        Commentary commentary = mock(Commentary.class); // 🔥 FIX ICI

        when(session.getAttribute("userId")).thenReturn(userId);

        when(commentaryService.getCommentaryById(commentId)).thenReturn(commentary);

        when(commentary.getAuthor()).thenReturn(user);
        when(commentary.getActivity()).thenReturn(activity);

        when(user.getId()).thenReturn(userId);
        when(activity.getId()).thenReturn(activityId);

        String view = activityController.updateReaction(commentId, ReactionType.LIKE, session);

        assertEquals("redirect:/activities/details/10", view);

        verify(commentaryService).createCommentary(commentary);
        verify(commentary).setReaction(ReactionType.LIKE);
    }

    @Test
    void updateReactionShouldNotAllowIfNotAuthor() {

        Long userId = 1L;
        Long commentId = 5L;

        User author = mock(User.class);
        Activity activity = mock(Activity.class);
        Commentary commentary = mock(Commentary.class);

        when(session.getAttribute("userId")).thenReturn(userId);

        when(commentaryService.getCommentaryById(commentId)).thenReturn(commentary);

        when(commentary.getAuthor()).thenReturn(author);
        when(commentary.getActivity()).thenReturn(activity);

        when(author.getId()).thenReturn(2L);
        when(activity.getId()).thenReturn(10L);

        String view = activityController.updateReaction(commentId, ReactionType.LIKE, session);

        assertEquals("redirect:/activities/details/10", view);

        verify(commentaryService, never()).createCommentary(any());
    }

    @Test
    void updateReactionShouldRedirectWhenUserNotLoggedIn() {

        when(session.getAttribute("userId")).thenReturn(null);

        String view = activityController.updateReaction(5L, ReactionType.LIKE, session);

        assertEquals("redirect:/users/login", view);
    }

    @Test
    void updateReactionShouldRedirectWhenCommentNotFound() {

        Long userId = 1L;

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentaryService.getCommentaryById(5L)).thenReturn(null);

        String view = activityController.updateReaction(5L, ReactionType.LIKE, session);

        assertEquals("redirect:/activities", view);
    }

    @Test
    void updateReactionShouldRejectWhenNotAuthor() {

        Long userId = 1L;

        User author = mock(User.class);
        Activity activity = mock(Activity.class);
        Commentary commentary = mock(Commentary.class);

        when(session.getAttribute("userId")).thenReturn(userId);

        when(commentaryService.getCommentaryById(5L)).thenReturn(commentary);

        when(commentary.getAuthor()).thenReturn(author);
        when(commentary.getActivity()).thenReturn(activity);

        when(author.getId()).thenReturn(2L);
        when(activity.getId()).thenReturn(10L);

        String view = activityController.updateReaction(5L, ReactionType.LIKE, session);

        assertEquals("redirect:/activities/details/10", view);

        verify(commentaryService, never()).createCommentary(any());
    }


    // ──────────────────────────────────────────────────────────────
    // GET /activities/listActivitiesUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void listActivitiesUser_shouldReturnListViewWithActivitiesSortedByDateDesc() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        Activity older = new Activity();
        older.setDate(LocalDate.of(2024, 1, 1));
        Activity newer = new Activity();
        newer.setDate(LocalDate.of(2024, 6, 1));
        
        when(activityService.getActivitiesByUserId(userId))
            .thenReturn(Arrays.asList(older, newer));

        String view = activityController.listActivitiesUser(model, session);

        assertEquals("activities/list", view);

        ArgumentCaptor<List<Activity>> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("activities"), captor.capture());
        List<Activity> sortedActivities = captor.getValue();

        assertEquals(2, sortedActivities.size());
        assertEquals(newer, sortedActivities.get(0));
        assertEquals(older, sortedActivities.get(1));
    }

    @Test
    void listActivitiesUserShouldReturnFormLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = activityController.listActivitiesUser(model, session);

        assertEquals("formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }
}
