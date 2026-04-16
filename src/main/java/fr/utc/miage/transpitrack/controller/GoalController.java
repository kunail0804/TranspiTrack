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

@Controller
@RequestMapping("/users")
public class GoalController {

    private static final String REDIRECT_LOGIN = "redirect:/users/formLogin";
    private static final String REDIRECT_GOALS = "redirect:/users/consultationGoals";
    private static final String SESSION_USER_ID = "userId";
    private static final String MSG = "message";
    private static final String NEEDCONNEXION = "Il faut être connecte !";

    @Autowired
    UserService userService;

    @Autowired
    GoalService goalService;

    @Autowired
    SportService sportService;

    // ──────────────────────────────────────────────────────────────
    // Objectifs
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, NEEDCONNEXION);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("goals", user.getGoals());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("temporalities", Temporality.values());

        return "goals/listGoals";
    }

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
