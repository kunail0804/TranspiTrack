package fr.utc.miage.transpitrack.controller;

import fr.utc.miage.transpitrack.dto.WeatherResponse;
import fr.utc.miage.transpitrack.model.jpa.WeatherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWeatherForUser(@PathVariable Long userId) {
        try {
            WeatherResponse weather = weatherService.getWeatherForUser(userId);
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
}