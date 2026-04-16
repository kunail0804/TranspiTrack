package fr.utc.miage.transpitrack.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.utc.miage.transpitrack.dto.WeatherResponse;
import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;
import fr.utc.miage.transpitrack.model.jpa.WeatherService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller that serves the user dashboard at {@code /users/dashboard}.
 * <p>
 * Computes per-sport activity statistics (total distance, total duration, session count,
 * total calories) and optionally fetches weather data for the user's city. All computed
 * data is exposed to the Thymeleaf template via model attributes.
 * </p>
 */
@Controller
@RequestMapping("/users/dashboard")
public class DashboardController {

    /** Service for retrieving the user's activities. */
    @Autowired
    ActivityService activityService;

    /** Service for fetching current weather for the user's city. */
    @Autowired
    WeatherService weatherService;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public DashboardController() {
        // Spring-managed bean.
    }

    /**
     * Renders the dashboard page for the currently authenticated user.
     * <p>
     * Aggregates activity data by sport and attempts to fetch weather. If weather
     * retrieval fails (city not set, API unavailable), the weather widget is simply
     * omitted from the page without propagating the error.
     * </p>
     *
     * @param model   the Spring MVC model populated with statistics and weather data
     * @param session the current HTTP session; must contain {@code "userId"}
     * @return the {@code users/dashboard} view, or the login form if not authenticated
     */
    @GetMapping("")
    public String dashboard(Model model, HttpSession session){

        Long userId = (Long) session.getAttribute("userId");

        if(userId == null){
            model.addAttribute("message", "Vous devez être connecté !");
            return "users/formLogin";
        }

        List<Activity> activityUser = new ArrayList<>(activityService.getActivitiesByUserId(userId));

        Map<Sport, Double> distanceBySport = new HashMap<>();
        Map<Sport, Integer> durationBySport = new HashMap<>();
        Map<Sport, Integer> countBySport = new HashMap<>();
        Map<String, Double> distanceBySportName = new HashMap<>();
        Map<Sport, Double> caloriesBySport = new HashMap<>();
        Map<String, Double> caloriesBySportName = new HashMap<>();
        double totalCalories = 0;

        for (Activity activity : activityUser) {
            Sport sport = activity.getSport();
            String sportName = sport.getName();
            double calories = activity.getTotalCaloriesAct();

            // Total distance per sport
            distanceBySport.merge(sport, activity.getDistance(), Double::sum);

            // Total duration per sport
            durationBySport.merge(sport, activity.getDuration(), Integer::sum);

            // Session count per sport
            countBySport.merge(sport, 1, Integer::sum);

            // Distance by sport name (for chart)
            distanceBySportName.merge(sportName, activity.getDistance(), Double::sum);

            // Calories per sport
            caloriesBySport.merge(sport, calories, Double::sum);

            // Calories by sport name (for chart)
            caloriesBySportName.merge(sportName, calories, Double::sum);

            totalCalories += calories;
        }

        model.addAttribute("distanceBySport", distanceBySport);
        model.addAttribute("durationBySport", durationBySport);
        model.addAttribute("countBySport", countBySport);
        model.addAttribute("distanceBySportName", distanceBySportName);
        model.addAttribute("caloriesBySport", caloriesBySport);
        model.addAttribute("caloriesBySportName", caloriesBySportName);
        model.addAttribute("totalCalories", totalCalories);

        try {
            WeatherResponse weather = weatherService.getWeatherForUser(userId);
            model.addAttribute("weather", weather);
        } catch (Exception _) {
            // City not set or API unavailable — weather widget hidden
        }

        return "users/dashboard";
    }
}
