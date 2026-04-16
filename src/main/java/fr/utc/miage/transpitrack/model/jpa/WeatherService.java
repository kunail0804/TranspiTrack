package fr.utc.miage.transpitrack.model.jpa;

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

import fr.utc.miage.transpitrack.dto.WeatherResponse;
import fr.utc.miage.transpitrack.exception.WeatherServiceException;
import fr.utc.miage.transpitrack.model.User;

/**
 * Service that fetches current weather and 7-day forecast data from the Open-Meteo API
 * for a given user's city, and assigns weather conditions to recorded activities.
 * <p>
 * Two external APIs are called in sequence:
 * <ol>
 *   <li>Open-Meteo Geocoding API — converts a city name to latitude/longitude.</li>
 *   <li>Open-Meteo Forecast API — retrieves temperature and weather code data.</li>
 * </ol>
 * Network and parsing errors are wrapped in {@link WeatherServiceException}.
 * </p>
 */
@Service
public class WeatherService {

    /** Repository used to load the user's city. */
    @Autowired
    private UserRepository userRepository;

    /** Shared HTTP client bean used for all outbound API calls. */
    @Autowired
    private HttpClient httpClient;

    /** JSON key for the daily forecast block in Open-Meteo responses. */
    private static final String STRINGDAILY = "daily";

    /** JSON key for the geocoding results array in Open-Meteo responses. */
    private static final String STRINGRESULTS = "results";

    /** JSON key for the current weather block in Open-Meteo responses. */
    private static final String STRINGCURRENTWEATHER = "current_weather";

    /** Jackson mapper used to parse API JSON responses. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** SLF4J logger for this service. */
    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    /**
     * Retrieves the current weather and a 7-day forecast for the city associated
     * with the given user.
     *
     * @param userId the ID of the user whose city is used for the lookup
     * @return a {@link WeatherResponse} containing the current temperature, condition,
     *         and forecast
     * @throws WeatherServiceException if the user is not found, has no city set,
     *         the city cannot be geocoded, the weather response is malformed, or
     *         a network error occurs
     */
    public WeatherResponse getWeatherForUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new WeatherServiceException("User not found"));

            String city = user.getCity();
            if (city == null || city.isBlank()) {
                throw new WeatherServiceException("User has not set a city.");
            }

            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=fr";

            HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).GET().build();
            HttpResponse<String> geoResponseStr = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode geoResponse = objectMapper.readTree(geoResponseStr.body());

            if (!geoResponse.has(STRINGRESULTS) || geoResponse.get(STRINGRESULTS).isEmpty()) {
                throw new WeatherServiceException("City not found: " + city);
            }

            JsonNode locationData = geoResponse.get(STRINGRESULTS).get(0);
            double lat = locationData.get("latitude").asDouble();
            double lon = locationData.get("longitude").asDouble();
            String resolvedCityName = locationData.get("name").asText();

            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                    + "&current_weather=true&daily=temperature_2m_max,temperature_2m_min&timezone=auto";

            HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).GET().build();
            HttpResponse<String> weatherResponseStr = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode weatherData = objectMapper.readTree(weatherResponseStr.body());

            if (!weatherData.has(STRINGCURRENTWEATHER) || !weatherData.has(STRINGDAILY)) {
                throw new WeatherServiceException("Weather response format error");
            }

            double currentTemp = weatherData.get(STRINGCURRENTWEATHER).get("temperature").asDouble();
            int weatherCode = weatherData.get(STRINGCURRENTWEATHER).get("weathercode").asInt();
            String condition = interpretWeatherCode(weatherCode);

            JsonNode daily = weatherData.get(STRINGDAILY);
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

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherServiceException("Weather error", e);

        } catch (IOException e) {
            throw new WeatherServiceException("Weather error", e);
        }
    }

    /**
     * Converts an Open-Meteo WMO weather code to a human-readable French condition string.
     *
     * @param code the WMO weather interpretation code
     * @return a French condition label such as {@code "Ciel dégagé"}, {@code "Pluie"}, etc.;
     *         defaults to {@code "Nuageux"} for unrecognised codes
     */
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

    /**
     * Attempts to assign weather data (temperature and condition) to the given activity
     * based on its city and date.
     * <p>
     * Does nothing if the activity has no city or no date. Geocoding and API failures
     * are caught and logged silently so that activity saving is never blocked by a
     * weather lookup failure.
     * </p>
     *
     * @param activity the activity to enrich with weather data
     */
    public void assignWeatherToActivity(fr.utc.miage.transpitrack.model.Activity activity) {
        if (activity.getCity() == null || activity.getCity().isBlank() || activity.getDate() == null) {
            return;
        }

        try {
            String encodedCity = URLEncoder.encode(activity.getCity(), StandardCharsets.UTF_8);
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=fr";

            HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).GET().build();
            HttpResponse<String> geoResponseStr = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode geoResponse = objectMapper.readTree(geoResponseStr.body());

            if (!geoResponse.has(STRINGRESULTS) || geoResponse.get(STRINGRESULTS).isEmpty()) {
                return;
            }

            JsonNode locationData = geoResponse.get(STRINGRESULTS).get(0);
            double lat = locationData.get("latitude").asDouble();
            double lon = locationData.get("longitude").asDouble();

            String dateStr = activity.getDate().toString();
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                    + "&start_date=" + dateStr + "&end_date=" + dateStr + "&daily=temperature_2m_max,weathercode&timezone=auto";

            HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).GET().build();
            HttpResponse<String> weatherResponseStr = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode weatherData = objectMapper.readTree(weatherResponseStr.body());

            if (weatherData.has(STRINGDAILY)) {
                JsonNode daily = weatherData.get(STRINGDAILY);
                double temp = daily.get("temperature_2m_max").get(0).asDouble();
                int code = daily.get("weathercode").get(0).asInt();

                activity.setTemperature(temp);
                activity.setWeatherCondition(interpretWeatherCode(code));
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.error("Error assigning weather to activity: " + e.getMessage(), e);
        }
    }
}
