package fr.utc.miage.transpitrack.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class WeatherResponseTest {

    @Test
    void forecastDayGettersShouldReturnConstructedValues() {
        WeatherResponse.ForecastDay day = new WeatherResponse.ForecastDay("2024-06-01", 12.5, 24.3);

        assertEquals("2024-06-01", day.getDate());
        assertEquals(12.5, day.getMinTemp(), 0.001);
        assertEquals(24.3, day.getMaxTemp(), 0.001);
    }
}
