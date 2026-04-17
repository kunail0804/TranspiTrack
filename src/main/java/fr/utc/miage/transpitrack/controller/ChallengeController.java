package fr.utc.miage.transpitrack.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

/**
 * Spring MVC controller handling challenge-related HTTP requests under
 * {@code /challenges}.
 * <p>
 * Covers challenge creation, listing (public and friends' challenges), joining,
 * viewing details, and submitting or updating scores. The leaderboard is
 * ordered by descending score.
 * </p>
 */
@Controller
@RequestMapping("/challenges")
public class ChallengeController {

    /** Redirect prefix for the challenge details page. */
    private static final String REDIRECT_CHALLENGE_DETAILS = "redirect:/challenges/details/";

    /** Service for challenge CRUD operations. */
    @Autowired
    ChallengeService challengeService;

    /** Service for user retrieval and updates. */
    @Autowired
    UserService userService;

    /** Service for sport retrieval. */
    @Autowired
    SportService sportService;

    /** Service for retrieving the user's accepted friendships. */
    @Autowired
    FriendshipService friendshipService;

    /** Service for submitting and retrieving challenge scores. */
    @Autowired
    ChallengeScoreService challengeScoreService;

    /** Redirect to the login form when the user is not authenticated. */
    private static final String REDIRECTFORMLOGIN = "redirect:/users/formLogin";

    /** Redirect to the challenge list. */
    private static final String REDIRECTLIST = "redirect:/challenges/list";

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public ChallengeController() {
        // Spring-managed bean.
    }

    /**
     * Displays the challenge creation form.
     *
     * @param session the current HTTP session
     * @param model   the Spring MVC model
     * @return the {@code challenge/createChallenge} view, or a redirect to login
     */
    @GetMapping("/formCreate")
    public String formCreateChallenge(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        model.addAttribute("sports", sportService.getAllSports());
        return "challenge/createChallenge";
    }

    /**
     * Processes the challenge creation form.
     *
     * @param title        the display title of the new challenge
     * @param durationDays the duration in days (must be non-negative)
     * @param visibility   the visibility setting ({@code "PUBLIC"} or {@code "PRIVATE"})
     * @param sportId      the ID of the targeted sport
     * @param session      the current HTTP session
     * @param model        the Spring MVC model
     * @return a redirect to the dashboard on success, or back to the form on validation error
     */
    @PostMapping("/create")
    public String createChallenge(@RequestParam("title") String title,
                                  @RequestParam("durationDays") int durationDays,
                                  @RequestParam("visibility") String visibility,
                                  @RequestParam("sportId") Long sportId,
                                  HttpSession session,
                                  Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }

        if (durationDays < 0) {
            model.addAttribute("errorMessage", "Durée négative, veuillez insérer une durée valide");
            return "challenge/createChallenge";
        }

        User creator = userService.getUserById(userId);
        Sport sport = sportService.getSportById(sportId);
        Duration duration = Duration.ofDays(durationDays);

        Challenge newChallenge = new Challenge(title, visibility, duration, creator, sport);
        challengeService.createChallenge(newChallenge);

        return "redirect:/users/dashboard";
    }

    /**
     * Displays the challenge list, including public challenges, challenges created
     * by friends, and the user's own challenges.
     *
     * @param session the current HTTP session
     * @param model   the Spring MVC model
     * @return the {@code challenge/listChallenges} view, or a redirect to login
     */
    @GetMapping("/list")
    public String listChallenges(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }

        List<Friendship> friendships = friendshipService.getMyFriendships(userId);
        List<User> friends = friendships.stream()
                .map(f -> f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester())
                .toList();
        List<Challenge> friendsChallenges = new ArrayList<>();
        for (User friend : friends) {
            friendsChallenges.addAll(friend.getCreatedChallenges());
        }
        List<Challenge> challenges = challengeService.getChallengesByVisibility("PUBLIC");
        List<Challenge> myChallenges = challengeService.getChallengesByCreatorId(userId);

        model.addAttribute("challenges", challenges);
        model.addAttribute("friendsChallenges", friendsChallenges);
        model.addAttribute("myChallenges", myChallenges);
        model.addAttribute("participatingIds", userService.getParticipatingChallengeIds(userId));

        return "challenge/listChallenges";
    }

    /**
     * Processes a request for the authenticated user to join an existing challenge.
     * Does nothing and redirects with a message if the user is already participating.
     *
     * @param idChallenge the ID of the challenge to join
     * @param model       the Spring MVC model
     * @param session     the current HTTP session
     * @return a success view on first join, or a redirect to the list if already participating
     */
    @PostMapping("/joinChallenge")
    public String getMethodName(@RequestParam("challengeId") Long idChallenge,
                                Model model,
                                HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }

        if (idChallenge == null) {
            return REDIRECTLIST;
        }

        User user = userService.getUserById(userId);
        Challenge challenge = challengeService.getChallengeById(idChallenge);

        if (user.isAlreadyJoinChallenge(challenge)) {
            model.addAttribute("message", "Vous participer deja a ce challenge !");
            return REDIRECTLIST;
        }

        if (challenge.getCreator() == user) {
            model.addAttribute("message", "Vous participer deja a ce challenge car vous l'avez créer !");
            return REDIRECTLIST;
        }

        user.addChallenge(challenge);
        userService.updateUser(user);

        return REDIRECT_CHALLENGE_DETAILS + idChallenge;
    }

    /**
     * Displays the detail page for a single challenge, including the leaderboard
     * and the current user's score.
     *
     * @param id      the ID of the challenge to display
     * @param session the current HTTP session
     * @param model   the Spring MVC model
     * @return the {@code challenge/detailChallenge} view, or a redirect to login
     */
    @GetMapping("/details/{id}")
    public String showChallengeDetails(@PathVariable("id") Long id, HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        User currentUser = userService.getUserById(userId);
        Challenge challenge = challengeService.getChallengeById(id);

        boolean isCreator     = currentUser.isTheCreatorOfTheChallenge(challenge);
        boolean isParticipant = currentUser.isAlreadyJoinChallenge(challenge);
        boolean canAddScore   = isCreator || isParticipant;

        ChallengeScore userScore = canAddScore
                ? challengeScoreService.getScoreByUserAndChallenge(currentUser, challenge)
                : null;
        List<ChallengeScore> classementTrie = challengeScoreService.getClassementParChallenge(id);

        model.addAttribute("challenge", challenge);
        model.addAttribute("canAddScore", canAddScore);
        model.addAttribute("canJoin", !isCreator && !isParticipant);
        model.addAttribute("userScore", userScore);
        model.addAttribute("listeScores", classementTrie);
        return "challenge/detailChallenge";
    }

    /**
     * Submits or updates the score of the authenticated user for a challenge.
     * Only the creator or a joined participant can submit a score.
     *
     * @param id      the ID of the challenge
     * @param score   the score value to submit
     * @param session the current HTTP session
     * @return a redirect to the challenge details page
     */
    @PostMapping("/details/{id}/addScore")
    public String addScore(@PathVariable("id") Long id,
                           @RequestParam("score") double score,
                           HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        Challenge challenge = challengeService.getChallengeById(id);
        User currentUser = userService.getUserById(userId);

        boolean canAddScore = currentUser.isTheCreatorOfTheChallenge(challenge)
                || currentUser.isAlreadyJoinChallenge(challenge);

        if (canAddScore) {
            ChallengeScore existing = challengeScoreService.getScoreByUserAndChallenge(currentUser, challenge);
            if (existing != null) {
                existing.setScore(score);
                challengeScoreService.addScore(existing);
            } else {
                challengeScoreService.addScore(new ChallengeScore(currentUser, challenge, score));
            }
        }
        return REDIRECT_CHALLENGE_DETAILS + id;
    }

    /**
     * Returns the ID of the currently authenticated user from the session,
     * or {@code null} if not logged in.
     *
     * @param session the current HTTP session
     * @return the user ID, or {@code null}
     */
    public Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
