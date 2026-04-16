package fr.utc.miage.transpitrack.Model.Jpa;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.exception.WeatherServiceException;

@Service
public class WeatherService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpClient httpClient;

    private static final String stringDaily = "daily";
    private static final String stringResults = "results";
    private static final String stringCurrentWeather = "current_weather";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public WeatherResponse getWeatherForUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new WeatherServiceException("Utilisateur non trouvé"));

            String city = user.getCity();
            if (city == null || city.isBlank()) {
                throw new WeatherServiceException("L'utilisateur n'a pas de ville renseignée.");
            }

            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=fr";

            HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).GET().build();
            HttpResponse<String> geoResponseStr = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode geoResponse = objectMapper.readTree(geoResponseStr.body());

            if (!geoResponse.has(stringResults) || geoResponse.get(stringResults).isEmpty()) {
                throw new WeatherServiceException("Ville introuvable : " + city);
            }

            JsonNode locationData = geoResponse.get(stringResults).get(0);
            double lat = locationData.get("latitude").asDouble();
            double lon = locationData.get("longitude").asDouble();
            String resolvedCityName = locationData.get("name").asText();

            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                    + "&current_weather=true&daily=temperature_2m_max,temperature_2m_min&timezone=auto";

            HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).GET().build();
            HttpResponse<String> weatherResponseStr = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode weatherData = objectMapper.readTree(weatherResponseStr.body());

            if (!weatherData.has(stringCurrentWeather) || !weatherData.has(stringDaily)) {
                throw new WeatherServiceException("Erreur de format de la réponse météo");
            }

            double currentTemp = weatherData.get(stringCurrentWeather).get("temperature").asDouble();
            int weatherCode = weatherData.get(stringCurrentWeather).get("weathercode").asInt();
            String condition = interpretWeatherCode(weatherCode);

            JsonNode daily = weatherData.get(stringDaily);
            List<WeatherResponse.ForecastDay> forecast = new ArrayList<>();

            int daysAvailable = daily.get("time").size();
            int daysToFetch = Math.min(daysAvailable, 7);

            for (int i = 0; i < daysToFetch; i++) {
                String date = daily.get("time").get(i).asText();
                double min = daily.get("temperature_2m_min").get(i).asDouble();
                double max = daily.get("temperature_2m_max").get(i).asDouble();
                forecast.add(new WeatherResponse.ForecastDay(date, min, max));
            }

            return new WeatherResponse(resolvedCityName, currentTemp, condition, forecast);

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new WeatherServiceException("Erreur météo", e);
        }
    }

    String interpretWeatherCode(int code) {
        if (code == 0) {
            return "Ciel dégagé";
        }
        if (code >= 1 && code <= 3) {
            return "Partiellement nuageux";
        }
        if (code >= 45 && code <= 48) {
            return "Brouillard";
        }
        if (code >= 51 && code <= 67) {
            return "Pluie";
        }
        if (code >= 71 && code <= 77) {
            return "Neige";
        }
        if (code >= 95 && code <= 99) {
            return "Orage";
        }
        return "Nuageux";
    }

    public void assignWeatherToActivity(fr.utc.miage.transpitrack.Model.Activity activity) {
        if (activity.getCity() == null || activity.getCity().isBlank() || activity.getDate() == null) {
            return;
        }

        try {
            String encodedCity = URLEncoder.encode(activity.getCity(), StandardCharsets.UTF_8);
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=fr";

            HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).GET().build();
            HttpResponse<String> geoResponseStr = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode geoResponse = objectMapper.readTree(geoResponseStr.body());

            if (!geoResponse.has(stringResults) || geoResponse.get(stringResults).isEmpty()) {
                return;
            }

            JsonNode locationData = geoResponse.get(stringResults).get(0);
            double lat = locationData.get("latitude").asDouble();
            double lon = locationData.get("longitude").asDouble();

            String dateStr = activity.getDate().toString();
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                    + "&start_date=" + dateStr + "&end_date=" + dateStr + "&daily=temperature_2m_max,weathercode&timezone=auto";

            HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).GET().build();
            HttpResponse<String> weatherResponseStr = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode weatherData = objectMapper.readTree(weatherResponseStr.body());

            if (weatherData.has(stringDaily)) {
                JsonNode daily = weatherData.get(stringDaily);
                double temp = daily.get("temperature_2m_max").get(0).asDouble();
                int code = daily.get("weathercode").get(0).asInt();

                activity.setTemperature(temp);
                activity.setWeatherCondition(interpretWeatherCode(code));
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.error("Erreur lors de l'attribution de la météo à l'activité : " + e.getMessage(), e);
        }
    }
}
