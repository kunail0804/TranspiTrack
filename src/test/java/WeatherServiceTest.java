
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import fr.utc.miage.transpitrack.Service.WeatherService;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WeatherService weatherService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setCity("Paris");
    }

    @Test
    void testGetWeather_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForUser(1L);
        });

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
    }

    @Test
    void testGetWeather_CityEmpty_ShouldThrowException() {
        testUser.setCity("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForUser(1L);
        });

        assertTrue(exception.getMessage().contains("pas de ville renseignée"));
    }

    @Test
    void testGetWeather_Success_ShouldReturnData() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        WeatherResponse response = weatherService.getWeatherForUser(1L);
        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertNotNull(response.getForecast());
        assertEquals(3, response.getForecast().size()); // Vérifie qu'on a bien nos 3 jours (Test #62)
        System.out.println("Température actuelle à Paris : " + response.getCurrentTemp());
    }
}