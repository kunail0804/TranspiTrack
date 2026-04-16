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

@Controller
@RequestMapping("/users/dashboard")
public class DashboardController {

    @Autowired
    ActivityService activityService;

    @Autowired
    WeatherService weatherService;

    @GetMapping("")
    public String dashboard(Model model,
                            HttpSession session){

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

            // Distance totale par sport
            distanceBySport.merge(sport, activity.getDistance(), Double::sum);

            // Durée totale par sport
            durationBySport.merge(sport, activity.getDuration(), Integer::sum);

            // Nombre de séances par sport
            countBySport.merge(sport, 1, Integer::sum);

            // Distance par nom de sport (pour le graphique)
            distanceBySportName.merge(sportName, activity.getDistance(), Double::sum);

            // Calories par sport
            caloriesBySport.merge(sport, calories, Double::sum);

            // Calories par nom de sport (pour le graphique)
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
            // Ville non renseignée ou API indisponible — widget absent
        }

        return "users/dashboard";
    }

}
