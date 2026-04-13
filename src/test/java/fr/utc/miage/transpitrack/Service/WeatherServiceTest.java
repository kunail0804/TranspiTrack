package fr.utc.miage.transpitrack.Service;

import java.time.LocalDate;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Model.Jpa.UserRepository;
import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private UserRepository userRepository;

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
    void getWeatherForUserShouldReturnWeatherAnd7DayForecastOnSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        WeatherResponse response = weatherService.getWeatherForUser(1L);

        assertNotNull(response, "La réponse météo ne doit pas être nulle");
        assertEquals("Paris", response.getCity());
        assertNotNull(response.getForecast(), "La liste des prévisions ne doit pas être nulle");
        assertEquals(7, response.getForecast().size(), "On doit récupérer les prévisions sur 7 jours");
    }

    @Test
    void assignWeatherToActivityShouldPopulateWeatherFields() {
        fr.utc.miage.transpitrack.Model.Activity activity = new fr.utc.miage.transpitrack.Model.Activity();
        activity.setCity("Toulouse");
        activity.setDate(LocalDate.now());
        weatherService.assignWeatherToActivity(activity);
        assertNotNull(activity.getTemperature(), "La température doit être récupérée depuis l'API");
        assertNotNull(activity.getWeatherCondition(), "La condition météo doit être récupérée depuis l'API");
        System.out.println("Test Activité - Météo à " + activity.getCity() + " : " 
                + activity.getTemperature() + "°C, " + activity.getWeatherCondition());
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
}
