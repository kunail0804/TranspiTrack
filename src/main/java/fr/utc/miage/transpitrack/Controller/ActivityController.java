package fr.utc.miage.transpitrack.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
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

        Long userId = (Long) session.getAttribute("userId");
        activity.setUser(userService.getUserById(userId));

        activityService.save(activity);
        return "redirect:/activities";
    }
}
