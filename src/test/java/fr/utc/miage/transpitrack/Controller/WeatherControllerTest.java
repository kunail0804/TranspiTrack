package fr.utc.miage.transpitrack.Controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void getWeatherForUserShouldReturn200WithWeatherDataWhenUserExists() {
        WeatherResponse weather = new WeatherResponse("Paris", 18.5, "Ensoleillé", List.of());
        when(weatherService.getWeatherForUser(1L)).thenReturn(weather);

        ResponseEntity<?> response = weatherController.getWeatherForUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(weather, response.getBody());
    }

    @Test
    void getWeatherForUserShouldReturn400WhenUserNotFound() {
        when(weatherService.getWeatherForUser(99L))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        ResponseEntity<?> response = weatherController.getWeatherForUser(99L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Utilisateur non trouvé"));
    }

    @Test
    void getWeatherForUserShouldReturn400WhenCityIsEmpty() {
        when(weatherService.getWeatherForUser(2L))
                .thenThrow(new RuntimeException("pas de ville renseignée"));

        ResponseEntity<?> response = weatherController.getWeatherForUser(2L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("pas de ville renseignée"));
    }
}
