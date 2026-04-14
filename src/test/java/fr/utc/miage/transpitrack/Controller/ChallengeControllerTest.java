package fr.utc.miage.transpitrack.Controller;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
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
    @Mock private Model model;
    @Mock private HttpSession session;

    @InjectMocks
    private ChallengeController challengeController;


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
    }


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
        when(challengeService.createChallenge(any(Challenge.class))).thenReturn(new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), creator, sport));

        String view = challengeController.createChallenge("Run 5km", 7, "PUBLIC", 1L, session, model);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).getUserById(1L);
        verify(challengeService).createChallenge(any(Challenge.class));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /challenges/list
    // ──────────────────────────────────────────────────────────────

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
        when(challengeService.getAllChallenges()).thenReturn(List.of(c1, c2));

        String view = challengeController.listChallenges(session, model);

        assertEquals("challenge/listChallenges", view);
        verify(challengeService).getAllChallenges();
        verify(model).addAttribute("challenges", List.of(c1, c2));
    }

    @Test
    void testCreateChallenge_WithNegativeDuration_ShouldReturnError() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String viewName = challengeController.createChallenge(
                "Course rapide", 
                -5, 
                "Public", 
                1L,
                session, 
                model
        );

        assertEquals("challenge/createChallenge", viewName);

        verify(model).addAttribute("errorMessage", "Durée négative, veuillez insérer une durée valide");

        verify(challengeService, never()).createChallenge(any(Challenge.class));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /challenges/details/{id}
    // ──────────────────────────────────────────────────────────────

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
