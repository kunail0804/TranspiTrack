package fr.utc.miage.transpitrack.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Sport;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users/dashboard")
public class DashboardController {

    @Autowired
    ActivityService activityService;

    @GetMapping("")
    public String dashboard(Model model,
                            HttpSession session){

        Long userId = (Long) session.getAttribute("userId");

        if(userId == null){
            model.addAttribute("message", "Vous devez être connecté !");
            return "users/formLogin";
        }

        List<Activity> activityUser = activityService.getActivitiesByUserId(userId);

        Map<Sport, Double> distanceBySport = new HashMap<>();
        Map<Sport, Integer> durationBySport = new HashMap<>();
        Map<Sport, Integer> countBySport = new HashMap<>();
        Map<String, Double> distanceBySportName = new HashMap<>();

        for (Activity activity : activityUser) {
            Sport sport = activity.getSport();

            //Distance totale parcouru par sport
            if(!distanceBySport.containsKey(sport)){
                distanceBySport.put(sport, activity.getDistance());
            }else{
                distanceBySport.put(sport, distanceBySport.get(sport) + activity.getDistance());
            }

            //Duree totale effectuer par sport
            if(!durationBySport.containsKey(sport)){
                durationBySport.put(sport, activity.getDuration());
            }else{
                durationBySport.put(sport, durationBySport.get(sport) + activity.getDuration());
            }

             //Nombre de séance totale effectuer par sport
            if(!countBySport.containsKey(sport)){
                countBySport.put(sport, 1);
            }else{
                countBySport.put(sport, countBySport.get(sport) + 1);
            }

            String sportName = activity.getSport().getName();

            if(!distanceBySportName.containsKey(sportName)){
                distanceBySportName.put(sportName, activity.getDistance());
            }else{
                distanceBySportName.put(sportName, distanceBySportName.get(sportName) + activity.getDistance());
            }

        }

        
        model.addAttribute("distanceBySport", distanceBySport);
        model.addAttribute("durationBySport", durationBySport);
        model.addAttribute("countBySport", countBySport);
        model.addAttribute("distanceBySportName", distanceBySportName);

        return "users/dashboard";
    }

}
