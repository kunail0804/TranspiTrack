package fr.utc.miage.transpitrack.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Model.Jpa.UserRepository;
import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private WeatherService weatherService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setCity("Paris");
    }

    @Test
    void getWeatherForUserShouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                weatherService.getWeatherForUser(1L));

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
    }

    @Test
    void getWeatherForUserShouldThrowWhenCityIsEmpty() {
        testUser.setCity("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(RuntimeException.class, () ->
                weatherService.getWeatherForUser(1L));

        assertTrue(exception.getMessage().contains("pas de ville renseignée"));
    }

    @Test
    void getWeatherForUserShouldThrowWhenCityIsNull() {
        testUser.setCity(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(RuntimeException.class, () ->
                weatherService.getWeatherForUser(1L));

        assertTrue(exception.getMessage().contains("pas de ville renseignée"));
    }

    @Test
    void interpretWeatherCodeShouldReturnCielDegageForCode0() {
        assertEquals("Ciel dégagé", weatherService.interpretWeatherCode(0));
    }

    @Test
    void interpretWeatherCodeShouldReturnPartiellemntNuageuxForCodes1To3() {
        assertEquals("Partiellement nuageux", weatherService.interpretWeatherCode(1));
        assertEquals("Partiellement nuageux", weatherService.interpretWeatherCode(2));
        assertEquals("Partiellement nuageux", weatherService.interpretWeatherCode(3));
    }

    @Test
    void interpretWeatherCodeShouldReturnBrouillardForCodes45To48() {
        assertEquals("Brouillard", weatherService.interpretWeatherCode(45));
        assertEquals("Brouillard", weatherService.interpretWeatherCode(48));
    }

    @Test
    void interpretWeatherCodeShouldReturnPluieForCodes51To67() {
        assertEquals("Pluie", weatherService.interpretWeatherCode(51));
        assertEquals("Pluie", weatherService.interpretWeatherCode(67));
    }

    @Test
    void interpretWeatherCodeShouldReturnNeigeForCodes71To77() {
        assertEquals("Neige", weatherService.interpretWeatherCode(71));
        assertEquals("Neige", weatherService.interpretWeatherCode(77));
    }

    @Test
    void interpretWeatherCodeShouldReturnOrageForCodes95To99() {
        assertEquals("Orage", weatherService.interpretWeatherCode(95));
        assertEquals("Orage", weatherService.interpretWeatherCode(99));
    }

    @Test
    void interpretWeatherCodeShouldReturnNuageuxForUnknownCode() {
        assertEquals("Nuageux", weatherService.interpretWeatherCode(10));
        assertEquals("Nuageux", weatherService.interpretWeatherCode(80));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getWeatherForUserShouldReturnWeatherAnd7DayForecastOnSuccess() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String geoJson = "{\"results\":[{\"latitude\":48.85,\"longitude\":2.35,\"name\":\"Paris\"}]}";
        String weatherJson = "{\"current_weather\":{\"temperature\":22.0,\"weathercode\":0},"
                + "\"daily\":{\"time\":[\"2024-01-01\",\"2024-01-02\",\"2024-01-03\","
                + "\"2024-01-04\",\"2024-01-05\",\"2024-01-06\",\"2024-01-07\"],"
                + "\"temperature_2m_min\":[10,11,12,13,14,15,16],"
                + "\"temperature_2m_max\":[20,21,22,23,24,25,26]}}";

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        HttpResponse<Object> weatherResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(weatherResp.body()).thenReturn(weatherJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp, weatherResp);

        WeatherResponse response = weatherService.getWeatherForUser(1L);

        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertEquals(22.0, response.getCurrentTemp(), 0.001);
        assertNotNull(response.getForecast());
        assertEquals(7, response.getForecast().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void assignWeatherToActivityShouldPopulateWeatherFields() throws Exception {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Toulouse");
        activity.setDate(LocalDate.now());

        String geoJson = "{\"results\":[{\"latitude\":43.60,\"longitude\":1.44,\"name\":\"Toulouse\"}]}";
        String weatherJson = "{\"daily\":{\"temperature_2m_max\":[25.0],\"weathercode\":[0]}}";

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        HttpResponse<Object> weatherResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(weatherResp.body()).thenReturn(weatherJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp, weatherResp);

        weatherService.assignWeatherToActivity(activity);

        assertNotNull(activity.getTemperature());
        assertNotNull(activity.getWeatherCondition());
        assertEquals(25.0, activity.getTemperature(), 0.001);
        assertEquals("Ciel dégagé", activity.getWeatherCondition());
    }

    @Test
    void assignWeatherToActivityShouldDoNothingIfCityIsEmpty() {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("");
        activity.setDate(LocalDate.now());
        weatherService.assignWeatherToActivity(activity);
        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    // ── WeatherResponse ────────────────────────────────────────────

    @Test
    void weatherResponseGetWeatherConditionShouldReturnCondition() {
        WeatherResponse response = new WeatherResponse("Lyon", 18.0, "Nuageux", List.of());
        assertEquals("Nuageux", response.getWeatherCondition());
    }

    // ── WeatherService : branches manquantes ───────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getWeatherForUserShouldThrowWhenCityNotFoundInGeocoding() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String geoJson = "{\"results\":[]}";
        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp);

        assertThrows(RuntimeException.class, () -> weatherService.getWeatherForUser(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getWeatherForUserShouldThrowWhenWeatherDataMissingFields() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String geoJson = "{\"results\":[{\"latitude\":48.85,\"longitude\":2.35,\"name\":\"Paris\"}]}";
        String weatherJson = "{}";

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        HttpResponse<Object> weatherResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(weatherResp.body()).thenReturn(weatherJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp, weatherResp);

        assertThrows(RuntimeException.class, () -> weatherService.getWeatherForUser(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void assignWeatherToActivityShouldDoNothingWhenCityNotFoundInGeocoding() throws Exception {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("VilleInconnue");
        activity.setDate(LocalDate.now());

        String geoJson = "{}";
        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp);

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    // ── branches manquantes supplémentaires ───────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getWeatherForUserShouldThrowWhenGeoResponseHasNoResultsKey() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn("{}");
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp);

        assertThrows(RuntimeException.class, () -> weatherService.getWeatherForUser(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getWeatherForUserShouldThrowWhenWeatherDataMissingDailyField() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String geoJson = "{\"results\":[{\"latitude\":48.85,\"longitude\":2.35,\"name\":\"Paris\"}]}";
        String weatherJson = "{\"current_weather\":{\"temperature\":22.0,\"weathercode\":0}}";

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        HttpResponse<Object> weatherResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(weatherResp.body()).thenReturn(weatherJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp, weatherResp);

        assertThrows(RuntimeException.class, () -> weatherService.getWeatherForUser(1L));
    }

    @Test
    void interpretWeatherCodeShouldReturnNuageuxForNegativeCode() {
        assertEquals("Nuageux", weatherService.interpretWeatherCode(-1));
    }

    @Test
    void interpretWeatherCodeShouldReturnNuageuxForCodeAbove99() {
        assertEquals("Nuageux", weatherService.interpretWeatherCode(100));
    }

    @Test
    void assignWeatherToActivityShouldDoNothingIfCityIsNull() {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity(null);
        activity.setDate(LocalDate.now());

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    @Test
    void assignWeatherToActivityShouldDoNothingIfDateIsNull() {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Paris");
        activity.setDate(null);

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    @Test
    @SuppressWarnings("unchecked")
    void assignWeatherToActivityShouldDoNothingWhenGeoResultsListIsEmpty() throws Exception {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Inconnue");
        activity.setDate(LocalDate.now());

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn("{\"results\":[]}");
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp);

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    @Test
    @SuppressWarnings("unchecked")
    void assignWeatherToActivityShouldNotUpdateWeatherWhenDailyKeyMissing() throws Exception {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Lyon");
        activity.setDate(LocalDate.now());

        String geoJson = "{\"results\":[{\"latitude\":45.74,\"longitude\":4.84,\"name\":\"Lyon\"}]}";
        String weatherJson = "{\"other\":\"data\"}";

        HttpResponse<Object> geoResp = (HttpResponse<Object>) mock(HttpResponse.class);
        HttpResponse<Object> weatherResp = (HttpResponse<Object>) mock(HttpResponse.class);
        when(geoResp.body()).thenReturn(geoJson);
        when(weatherResp.body()).thenReturn(weatherJson);
        when(httpClient.send(any(HttpRequest.class), any())).thenReturn(geoResp, weatherResp);

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }

    @Test
    void assignWeatherToActivityShouldSuppressExceptionAndNotUpdateWeather() throws Exception {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Paris");
        activity.setDate(LocalDate.now());
        when(httpClient.send(any(HttpRequest.class), any())).thenThrow(new IOException("connection refused"));

        weatherService.assignWeatherToActivity(activity);

        assertNull(activity.getTemperature());
        assertNull(activity.getWeatherCondition());
    }
}
