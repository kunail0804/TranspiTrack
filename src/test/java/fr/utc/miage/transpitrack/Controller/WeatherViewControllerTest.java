package fr.utc.miage.transpitrack.Controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Dto.WeatherResponse;
import fr.utc.miage.transpitrack.Service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherViewControllerTest {

    @Mock private WeatherService weatherService;
    @Mock private Model model;

    @InjectMocks
    private WeatherViewController weatherViewController;

    @Test
    void showWeatherPageShouldReturnWeatherViewWithDataWhenUserExists() {
        WeatherResponse weather = new WeatherResponse("Lyon", 22.0, "Nuageux", List.of());
        when(weatherService.getWeatherForUser(1L)).thenReturn(weather);

        String view = weatherViewController.showWeatherPage(1L, model);

        assertEquals("weather", view);
        verify(model).addAttribute("weather", weather);
    }

    @Test
    void showWeatherPageShouldReturnErrorViewWhenUserNotFound() {
        when(weatherService.getWeatherForUser(99L))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        String view = weatherViewController.showWeatherPage(99L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "Utilisateur non trouvé");
    }

    @Test
    void showWeatherPageShouldReturnErrorViewWhenCityIsEmpty() {
        when(weatherService.getWeatherForUser(2L))
                .thenThrow(new RuntimeException("pas de ville renseignée"));

        String view = weatherViewController.showWeatherPage(2L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "pas de ville renseignée");
    }
}
