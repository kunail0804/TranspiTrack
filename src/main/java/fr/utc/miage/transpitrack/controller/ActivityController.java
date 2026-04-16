package fr.utc.miage.transpitrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Commentary;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.ReactionType;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;
import fr.utc.miage.transpitrack.model.jpa.BadgeService;
import fr.utc.miage.transpitrack.model.jpa.CommentaryService;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import fr.utc.miage.transpitrack.model.jpa.WeatherService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller handling all activity-related HTTP requests under
 * {@code /activities}.
 * <p>
 * Covers listing a user's activities, recording a new activity (with automatic
 * weather enrichment and badge evaluation), viewing activity details,
 * posting commentaries, and updating emoji reactions on commentaries.
 * </p>
 */
@Controller
@RequestMapping("/activities")
public class ActivityController {

    /** Session attribute key that stores the authenticated user's ID. */
    private static final String SESSION_USER_ID = "userId";

    /** Service for activity CRUD operations. */
    @Autowired
    private ActivityService activityService;

    /** Service for user retrieval and updates. */
    @Autowired
    private UserService userService;

    /** Service for sport retrieval. */
    @Autowired
    private SportService sportService;

    /** Service for commentary CRUD operations. */
    @Autowired
    private CommentaryService commentaryService;

    /** Service for fetching and assigning weather data to activities. */
    @Autowired
    private WeatherService weatherService;

    /** Service for checking and awarding badges after an activity is saved. */
    @Autowired
    private BadgeService badgeService;

    /** Redirect prefix for the activity details page. */
    private static final String REDIRECTDETAILS = "redirect:/activities/details/";

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public ActivityController() {
        // Spring-managed bean.
    }

    /**
     * Displays the list of activities for the currently logged-in user,
     * sorted by date descending.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code activities/list} view, or a redirect to login if not authenticated
     */
    @RequestMapping("")
    public String listActivities(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return "redirect:/users/formLogin";
        }
        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        activities.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
        model.addAttribute("activities", activities);
        return "activities/list";
    }

    /**
     * Displays the form for recording a new activity.
     *
     * @param error   optional error code populated on validation failure (may be {@code null})
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code activities/add} view
     */
    @GetMapping("/add")
    public String addActivity(@RequestParam(required = false) String error, Model model, HttpSession session) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("sports", sportService.getAllSports());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "activities/add";
    }

    /**
     * Processes the new-activity form submission.
     * <p>
     * Validates duration, date, and distance; assigns weather data; saves the activity;
     * and evaluates badge criteria.
     * </p>
     *
     * @param activity the activity populated from the form
     * @param sport    the ID of the selected sport
     * @param session  the current HTTP session
     * @return a redirect to the dashboard on success, or back to the form with an error code
     */
    @PostMapping("/add")
    public String saveActivity(
            @ModelAttribute Activity activity,
            @RequestParam Long sport,
            HttpSession session
    ) {
        int duration = activity.getDuration();
        if (duration <= 0) {
            return "redirect:/activities/add?error=invalid_duration";
        }

        if (activity.getDate() == null) {
            return "redirect:/activities/add?error=invalid_date";
        }

        double distance = activity.getDistance();

        if (distance < 0) {
            return "redirect:/activities/add?error=invalid_distance";
        }

        Sport selectedSport = sportService.getSportById(sport);
        activity.setSport(selectedSport);

        Long userId = getUserId(session);
        activity.setUser(userService.getUserById(userId));

        weatherService.assignWeatherToActivity(activity);

        activityService.save(activity);

        User user = userService.getUserById(userId);
        badgeService.checkAndAwardBadges(user, activityService.getActivitiesByUserId(userId));

        return "redirect:/users/dashboard";
    }

    /**
     * Displays the detail page for a single activity, including its commentaries.
     *
     * @param id      the ID of the activity to display
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code activities/details} view, or a redirect on error
     */
    @GetMapping("/details/{id}")
    public String getActivityDetails(@PathVariable Long id, Model model, HttpSession session) {
        Long currentUserId = getUserId(session);

        if (currentUserId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte";
        }

        User currentUser = userService.getUserById(currentUserId);

        if (currentUser == null) {
            return "redirect:/users/login?msg=Utilisateur introuvable";
        }

        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return "redirect:/activities";
        }

        List<Commentary> commentaries = commentaryService.getCommentariesByActivityId(id);

        boolean canComment = commentaryService
                .getCommentariesByAuthorIdAndActivityId(currentUserId, id)
                .isEmpty();

        model.addAttribute("author", activity.getUser());
        model.addAttribute("commentaries", commentaries);
        model.addAttribute("activity", activity);
        model.addAttribute("canComment", canComment);
        model.addAttribute("user", currentUser);
        model.addAttribute("commentary", new Commentary());

        return "activities/details";
    }

    /**
     * Processes a new commentary submission on an activity.
     * <p>
     * Enforces the one-commentary-per-user constraint before saving.
     * </p>
     *
     * @param commentary the commentary populated from the form
     * @param id         the ID of the activity being commented on
     * @param session    the current HTTP session
     * @return a redirect to the activity details page
     */
    @PostMapping("/comment/{id}")
    public String addCommentary(
            @ModelAttribute("commentary") Commentary commentary,
            @PathVariable Long id,
            HttpSession session
    ) {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour commenter";
        }

        Commentary existingCommentary = commentaryService.getCommentariesByAuthorIdAndActivityId(userId, id).stream().findFirst().orElse(null);
        if (existingCommentary != null) {
            return REDIRECTDETAILS + id + "?msg=Vous avez deja commente cette activite";
        }

        User user = userService.getUserById(userId);
        commentary.setAuthor(user);

        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return "redirect:/activities?msg=Activite non trouvee";
        }
        commentary.setActivity(activity);

        commentaryService.createCommentary(commentary);
        return REDIRECTDETAILS + activity.getId();
    }

    /**
     * Updates the emoji reaction on an existing commentary.
     * Only the author of the commentary is allowed to change the reaction.
     *
     * @param commentId the ID of the commentary to update
     * @param reaction  the new {@link ReactionType} to set
     * @param session   the current HTTP session
     * @return a redirect to the activity details page
     */
    @PostMapping("/comment/{commentId}/reaction")
    public String updateReaction(
            @PathVariable Long commentId,
            @RequestParam ReactionType reaction,
            HttpSession session
    ) {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/users/login";
        }

        Commentary commentary = commentaryService.getCommentaryById(commentId);

        if (commentary == null) {
            return "redirect:/activities";
        }

        // security: only the author can modify
        if (!commentary.getAuthor().getId().equals(userId)) {
            return REDIRECTDETAILS + commentary.getActivity().getId();
        }

        commentary.setReaction(reaction);
        commentaryService.createCommentary(commentary);

        return REDIRECTDETAILS + commentary.getActivity().getId();
    }

    /**
     * Returns the ID of the currently authenticated user from the session,
     * or {@code null} if the user is not logged in.
     *
     * @param session the current HTTP session
     * @return the user ID, or {@code null}
     */
    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute(SESSION_USER_ID);
    }
}
