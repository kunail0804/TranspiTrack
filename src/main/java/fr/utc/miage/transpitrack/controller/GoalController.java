package fr.utc.miage.transpitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.Temporality;
import fr.utc.miage.transpitrack.model.jpa.GoalService;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller handling training goal management under {@code /users}.
 * <p>
 * Allows authenticated users to view, create, update, and delete their
 * training goals. Each goal is associated with a sport and a recurrence
 * period ({@link Temporality}).
 * </p>
 */
@Controller
@RequestMapping("/users")
public class GoalController {

    /** Redirect to the login form when the user is not authenticated. */
    private static final String REDIRECT_LOGIN = "redirect:/users/formLogin";

    /** Redirect to the goal list after a successful operation. */
    private static final String REDIRECT_GOALS = "redirect:/users/consultationGoals";

    /** Session attribute key that stores the authenticated user's ID. */
    private static final String SESSION_USER_ID = "userId";

    /** Model attribute key for general messages. */
    private static final String MSG = "message";

    /** Message shown when an unauthenticated user attempts to access a protected resource. */
    private static final String LOGIN_REQUIRED = "Il faut être connecte !";

    /** Service for user retrieval and updates. */
    @Autowired
    UserService userService;

    /** Service for goal CRUD operations. */
    @Autowired
    GoalService goalService;

    /** Service for sport retrieval. */
    @Autowired
    SportService sportService;

    // ──────────────────────────────────────────────────────────────
    // Goals
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the list of training goals for the currently authenticated user.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code goals/listGoals} view, or a redirect to login if not authenticated
     */
    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, LOGIN_REQUIRED);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("goals", user.getGoals());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("temporalities", Temporality.values());

        return "goals/listGoals";
    }

    /**
     * Creates a new training goal for the currently authenticated user.
     *
     * @param textGoal    the description text of the goal
     * @param distance    the target distance in kilometres
     * @param sportId     the ID of the associated sport
     * @param temporality the recurrence period for the goal
     * @param session     the current HTTP session
     * @return a redirect to the goal list on success, or to login/goal list on validation failure
     */
    @PostMapping("/addGoal")
    public String addGoal(@RequestParam("goal") String textGoal,
                          @RequestParam("targetDistance") Double distance,
                          @RequestParam("sportId") Long sportId,
                          @RequestParam("temporality") Temporality temporality,
                          HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (textGoal == null || distance == null) return REDIRECT_GOALS;

        User user = userService.getUserById(userId);
        Sport sport = sportService.getSportById(sportId);
        Goal goal = new Goal(distance, textGoal, user, sport, temporality);
        goalService.createGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }

    /**
     * Updates an existing training goal with new values.
     *
     * @param goalId      the ID of the goal to update
     * @param textGoal    the new description text
     * @param distance    the new target distance in kilometres
     * @param sportId     the ID of the new associated sport
     * @param temporality the new recurrence period
     * @param session     the current HTTP session
     * @return a redirect to the goal list on success, or to login/goal list on validation failure
     */
    @PostMapping("/updateGoal")
    public String updateGoal(@RequestParam("goalId") Long goalId,
                             @RequestParam("goal") String textGoal,
                             @RequestParam("targetDistance") Double distance,
                             @RequestParam("sportId") Long sportId,
                             @RequestParam("temporality") Temporality temporality,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (textGoal == null || distance == null) return REDIRECT_GOALS;

        Goal goal = goalService.getGoalById(goalId);
        Sport sport = sportService.getSportById(sportId);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        goal.setGoalText(textGoal);
        goal.setTargetDistance(distance);
        goal.setSport(sport);
        goal.setTemporality(temporality);
        goalService.updateGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }

    /**
     * Deletes a training goal belonging to the currently authenticated user.
     *
     * @param goalId  the ID of the goal to delete
     * @param session the current HTTP session
     * @return a redirect to the goal list on success, or to login if not authenticated
     */
    @PostMapping("/deleteGoal")
    public String deleteGoal(@RequestParam("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (goalId == null) return REDIRECT_GOALS;

        Goal goal = goalService.getGoalById(goalId);
        goalService.deleteGoal(goal);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }
}
