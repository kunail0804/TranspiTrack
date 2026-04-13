package fr.utc.miage.transpitrack.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Service.WeatherService;

@Controller 
public class WeatherViewController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather/{userId}")
    public String showWeatherPage(@PathVariable Long userId, Model model) {
        try {
            WeatherResponse weather = weatherService.getWeatherForUser(userId);
            
            model.addAttribute("weather", weather);
            return "dashboard"; 
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}