package fr.utc.miage.transpitrack.Controller;

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

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Commentary;
import fr.utc.miage.transpitrack.Model.Enum.ReactionType;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.BadgeService;
import fr.utc.miage.transpitrack.Model.Jpa.CommentaryService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Jpa.WeatherService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private SportService sportService;

    @Autowired
    private CommentaryService commentaryService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private BadgeService badgeService;

    private final String redirectDetails = "redirect:/activities/details/";

    @RequestMapping("")
    public String listActivities(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        activities.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
        model.addAttribute("activities", activities);
        return "activities/list";
    }

    @GetMapping("/add")
    public String addActivity(@RequestParam(required = false) String error, Model model, HttpSession session) {

        model.addAttribute("activity", new Activity());
        model.addAttribute("sports", sportService.getAllSports());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "activities/add";
    }

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
            return redirectDetails + id + "?msg=Vous avez deja commente cette activite";
        }

        User user = userService.getUserById(userId);
        commentary.setAuthor(user);

        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return "redirect:/activities?msg=Activite non trouvee";
        }
        commentary.setActivity(activity);

        commentaryService.createCommentary(commentary);
        return redirectDetails + activity.getId();
    }

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

        // sécurité : seul l'auteur peut modifier
        if (!commentary.getAuthor().getId().equals(userId)) {
            return redirectDetails + commentary.getActivity().getId();
        }

        commentary.setReaction(reaction);
        commentaryService.createCommentary(commentary);

        return redirectDetails + commentary.getActivity().getId();
    }

    @GetMapping("/listActivitiesUser")
    public String listActivitiesUser(Model model,
                                    HttpSession session){
        Long userId = getUserId(session);

        if(userId==null){
            model.addAttribute("message", "Il faut êtres connecter !");
            return "formLogin";
        }

        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        activities.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
        model.addAttribute("activities", activities);

        return "activities/list";
    }

    
    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute("userId");
    }
}
