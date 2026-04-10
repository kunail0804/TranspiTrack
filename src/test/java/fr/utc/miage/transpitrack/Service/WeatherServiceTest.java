package fr.utc.miage.transpitrack.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // ── getWeatherForUser ──────────────────────────────────────────

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

    // ── interpretWeatherCode ───────────────────────────────────────

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
}
