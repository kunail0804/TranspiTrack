package fr.utc.miage.transpitrack.controller;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import fr.utc.miage.transpitrack.model.Challenge;
import fr.utc.miage.transpitrack.model.ChallengeScore;
import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.jpa.ChallengeScoreService;
import fr.utc.miage.transpitrack.model.jpa.ChallengeService;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    @Mock private ChallengeService challengeService;
    @Mock private UserService userService;
    @Mock private SportService sportService;
    @Mock private FriendshipService friendshipService;
    @Mock private ChallengeScoreService challengeScoreService;
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

    @Test
    void listChallengesShouldIncludeChallengesCreatedByFriendWhenUserIsRequester() {
        Challenge friendChallenge = new Challenge();
        User friend = mock(User.class);
        when(friend.getCreatedChallenges()).thenReturn(List.of(friendChallenge));

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(1L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getRequester()).thenReturn(requester);
        when(friendship.getReceiver()).thenReturn(friend);

        when(session.getAttribute("userId")).thenReturn(1L);
        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(challengeService.getChallengesByVisibility("PUBLIC")).thenReturn(List.of());

        String view = challengeController.listChallenges(session, model);

        assertEquals("challenge/listChallenges", view);
        verify(model).addAttribute(eq("friendsChallenges"), any());
    }

    @Test
    void listChallengesShouldIncludeChallengesCreatedByFriendWhenUserIsReceiver() {
        Challenge friendChallenge = new Challenge();
        User friend = mock(User.class);
        when(friend.getCreatedChallenges()).thenReturn(List.of(friendChallenge));
        when(friend.getId()).thenReturn(99L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getRequester()).thenReturn(friend);

        when(session.getAttribute("userId")).thenReturn(1L);
        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(challengeService.getChallengesByVisibility("PUBLIC")).thenReturn(List.of());

        String view = challengeController.listChallenges(session, model);

        assertEquals("challenge/listChallenges", view);
        verify(model).addAttribute(eq("friendsChallenges"), any());
    }

    // ── POST /challenges/joinChallenge ─────────────────────────────

    @Test
    void joinChallengeShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = challengeController.getMethodName(1L, model, session);
        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void joinChallengeShouldRedirectToListWhenChallengeIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = challengeController.getMethodName(null, model, session);

        assertEquals("redirect:/challenges/list", view);
        verify(userService, never()).getUserById(any());
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
        User user = new User();
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        String view = challengeController.showChallengeDetails(1L, session, model);

        assertEquals("challenge/detailChallenge", view);
        verify(challengeService).getChallengeById(1L);
        verify(model).addAttribute("challenge", challenge);
    }

    @Test
    void showChallengeDetailsShouldSetCanAddScoreTrueWhenUserIsCreator() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(true);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("canAddScore", true);
    }

    @Test
    void showChallengeDetailsShouldSetCanAddScoreTrueWhenUserIsParticipant() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(false);
        when(user.isAlreadyJoinChallenge(challenge)).thenReturn(true);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("canAddScore", true);
    }

    @Test
    void showChallengeDetailsShouldSetCanAddScoreFalseWhenUserIsNeitherCreatorNorParticipant() {
        User user = new User();
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("canAddScore", false);
    }

    @Test
    void showChallengeDetailsShouldPassExistingUserScoreToModelWhenUserAlreadyHasScore() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        ChallengeScore existing = new ChallengeScore(user, challenge, 42.0);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(true);
        when(challengeScoreService.getScoreByUserAndChallenge(user, challenge)).thenReturn(existing);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("userScore", existing);
    }

    @Test
    void showChallengeDetailsShouldPassNullUserScoreToModelWhenUserHasNoScoreYet() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(true);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("userScore", null);
    }

    @Test
    void showChallengeDetailsShouldAddClassementToModel() {
        User user = new User();
        Challenge challenge = new Challenge();
        List<ChallengeScore> scores = List.of(new ChallengeScore(user, challenge, 10.0));
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(challengeScoreService.getClassementParChallenge(1L)).thenReturn(scores);

        challengeController.showChallengeDetails(1L, session, model);

        verify(model).addAttribute("listeScores", scores);
    }

    // ── POST /challenges/details/{id}/addScore ─────────────────────

    @Test
    void addScoreShouldRedirectToLoginWhenNotLoggedIn() {
        String view = challengeController.addScore(1L, 50.0, session);
        assertEquals("redirect:/users/formLogin", view);
        verify(challengeScoreService, never()).addScore(any());
    }

    @Test
    void addScoreShouldSaveScoreAndRedirectWhenUserIsCreator() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(true);

        String view = challengeController.addScore(1L, 88.0, session);

        assertEquals("redirect:/challenges/details/1", view);
        verify(challengeScoreService).addScore(any(ChallengeScore.class));
    }

    @Test
    void addScoreShouldSaveScoreAndRedirectWhenUserIsParticipant() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(false);
        when(user.isAlreadyJoinChallenge(challenge)).thenReturn(true);

        String view = challengeController.addScore(1L, 55.0, session);

        assertEquals("redirect:/challenges/details/1", view);
        verify(challengeScoreService).addScore(any(ChallengeScore.class));
    }

    @Test
    void addScoreShouldUpdateExistingScoreAndRedirectWhenUserAlreadyHasOne() {
        User user = mock(User.class);
        Challenge challenge = new Challenge();
        ChallengeScore existing = new ChallengeScore(user, challenge, 30.0);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);
        when(user.isTheCreatorOfTheChallenge(challenge)).thenReturn(true);
        when(challengeScoreService.getScoreByUserAndChallenge(user, challenge)).thenReturn(existing);

        String view = challengeController.addScore(1L, 75.0, session);

        assertEquals("redirect:/challenges/details/1", view);
        assertEquals(75.0, existing.getScore());
        verify(challengeScoreService).addScore(existing);
    }

    @Test
    void addScoreShouldNotSaveScoreAndRedirectWhenUserIsNeitherCreatorNorParticipant() {
        User user = new User();
        Challenge challenge = new Challenge();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(challengeService.getChallengeById(1L)).thenReturn(challenge);

        String view = challengeController.addScore(1L, 30.0, session);

        assertEquals("redirect:/challenges/details/1", view);
        verify(challengeScoreService, never()).addScore(any());
    }
}
