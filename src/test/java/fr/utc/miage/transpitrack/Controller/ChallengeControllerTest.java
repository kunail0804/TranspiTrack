package fr.utc.miage.transpitrack.Controller;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    @Mock private ChallengeService challengeService;
    @Mock private UserService userService;
    @Mock private SportService sportService;
    @Mock private FriendshipService friendshipService;
    @Mock private Model model;
    @Mock private HttpSession session;

    @InjectMocks
    private ChallengeController challengeController;

    // ── GET /challenges/formCreate ─────────────────────────────────

    @Test
    void formCreateChallengeShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.formCreateChallenge(session, model);
        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void formCreateChallengeShouldReturnCreateChallengeViewWhenLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = challengeController.formCreateChallenge(session, model);

        assertEquals("challenge/createChallenge", view);
        verify(model).addAttribute(eq("sports"), any());
    }

    // ── POST /challenges/create ────────────────────────────────────

    @Test
    void createChallengeShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.createChallenge("Run 5km", 7, "PUBLIC", 1L, session, model);
        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void createChallengeShouldCreateAndRedirectToDashboardWhenLoggedIn() {
        User creator = new User();
        Sport sport = new Sport();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(creator);
        when(sportService.getSportById(1L)).thenReturn(sport);
        when(challengeService.createChallenge(any(Challenge.class)))
                .thenReturn(new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), creator, sport));

        String view = challengeController.createChallenge("Run 5km", 7, "PUBLIC", 1L, session, model);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).getUserById(1L);
        verify(challengeService).createChallenge(any(Challenge.class));
    }

    @Test
    void createChallengeShouldReturnErrorViewWhenDurationIsNegative() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = challengeController.createChallenge("Course rapide", -5, "Public", 1L, session, model);

        assertEquals("challenge/createChallenge", view);
        verify(model).addAttribute("errorMessage", "Durée négative, veuillez insérer une durée valide");
        verify(challengeService, never()).createChallenge(any(Challenge.class));
    }

    // ── GET /challenges/list ───────────────────────────────────────

    @Test
    void listChallengesShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.listChallenges(session, model);
        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void listChallengesShouldReturnListViewWithChallengesWhenLoggedIn() {
        Challenge c1 = new Challenge();
        Challenge c2 = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of());
        when(challengeService.getChallengesByVisibility("PUBLIC")).thenReturn(List.of(c1, c2));

        String view = challengeController.listChallenges(session, model);

        assertEquals("challenge/listChallenges", view);
        verify(challengeService).getChallengesByVisibility("PUBLIC");
        verify(model).addAttribute(eq("challenges"), any());
        verify(model).addAttribute(eq("friendsChallenges"), any());
    }

    // ── POST /challenges/joinChallenge ─────────────────────────────

    @Test
    void joinChallengeShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.getMethodName(1L, model, session);
        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void joinChallengeShouldRedirectToListWhenAlreadyJoined() {
        User user = new User();
        Challenge challenge = new Challenge();
        user.addChallenge(challenge);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        String view = challengeController.getMethodName(1L, model, session);

        assertEquals("redirect:/challenges/list", view);
        verify(userService, never()).updateUser(any());
    }

    @Test
    void joinChallengeShouldRedirectToListWhenUserIsCreator() {
        User user = new User();
        Challenge challenge = new Challenge("Title", "PUBLIC", Duration.ofDays(7), user, null);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        String view = challengeController.getMethodName(1L, model, session);

        assertEquals("redirect:/challenges/list", view);
        verify(userService, never()).updateUser(any());
    }

    @Test
    void joinChallengeShouldAddChallengeAndReturnSuccessViewWhenValid() {
        User user = new User();
        User otherUser = new User();
        Challenge challenge = new Challenge("Title", "PUBLIC", Duration.ofDays(7), otherUser, null);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        String view = challengeController.getMethodName(1L, model, session);

        assertEquals("challenge/testSuccessJoin", view);
        verify(userService).updateUser(user);
    }

    // ── GET /challenges/details/{id} ──────────────────────────────

    @Test
    void showChallengeDetailsShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.showChallengeDetails(1L, session, model);
        assertEquals("redirect:/users/formLogin", view);
        verify(challengeService, never()).getChallengeById(any());
    }

    @Test
    void showChallengeDetailsShouldReturnDetailViewWhenLoggedIn() {
        Challenge mockChallenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(challengeService.getChallengeById(1L)).thenReturn(mockChallenge);

        String view = challengeController.showChallengeDetails(1L, session, model);

        assertEquals("challenge/detailChallenge", view);
        verify(challengeService).getChallengeById(1L);
        verify(model).addAttribute("challenge", mockChallenge);
    }
}
