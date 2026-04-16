package fr.utc.miage.transpitrack.dto;

import java.util.List;

/**
 * Data Transfer Object carrying the weather information displayed on the dashboard.
 * <p>
 * It contains the current temperature and condition for the user's city as well as
 * a 7-day forecast provided by the Open-Meteo API.
 * </p>
 */
public class WeatherResponse {

    /** Name of the city for which the weather was retrieved. */
    private String city;

    /** Current temperature in degrees Celsius. */
    private double currentTemp;

    /** Human-readable description of the current weather condition. */
    private String weatherCondition;

    /** Ordered list of forecast days, typically covering up to 7 days. */
    private List<ForecastDay> forecast;

    /**
     * Represents a single day's weather forecast entry.
     */
    public static class ForecastDay {

        /** ISO-8601 date string (e.g., {@code "2024-06-01"}). */
        private String date;

        /** Minimum temperature of the day in degrees Celsius. */
        private double minTemp;

        /** Maximum temperature of the day in degrees Celsius. */
        private double maxTemp;

        /**
         * Constructs a {@code ForecastDay} with all fields.
         *
         * @param date    the ISO-8601 date string
         * @param minTemp the minimum temperature in degrees Celsius
         * @param maxTemp the maximum temperature in degrees Celsius
         */
        public ForecastDay(String date, double minTemp, double maxTemp) {
            this.date = date;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
        }

        /**
         * Returns the ISO-8601 date string for this forecast day.
         *
         * @return the date string
         */
        public String getDate() {
            return date;
        }

        /**
         * Returns the minimum temperature for this forecast day.
         *
         * @return the minimum temperature in degrees Celsius
         */
        public double getMinTemp() {
            return minTemp;
        }

        /**
         * Returns the maximum temperature for this forecast day.
         *
         * @return the maximum temperature in degrees Celsius
         */
        public double getMaxTemp() {
            return maxTemp;
        }
    }

    /**
     * Constructs a fully populated {@code WeatherResponse}.
     *
     * @param city             the name of the city
     * @param currentTemp      the current temperature in degrees Celsius
     * @param weatherCondition a human-readable weather condition description
     * @param forecast         the ordered list of forecast days
     */
    public WeatherResponse(String city, double currentTemp, String weatherCondition, List<ForecastDay> forecast) {
        this.city = city;
        this.currentTemp = currentTemp;
        this.weatherCondition = weatherCondition;
        this.forecast = forecast;
    }

    /**
     * No-argument constructor required by certain frameworks and serializers.
     */
    public WeatherResponse() {
        // no-arg constructor required
    }

    /**
     * Returns the name of the city.
     *
     * @return the city name
     */
    public String getCity() { return city; }

    /**
     * Returns the current temperature.
     *
     * @return the current temperature in degrees Celsius
     */
    public double getCurrentTemp() { return currentTemp; }

    /**
     * Returns the human-readable weather condition description.
     *
     * @return the weather condition string
     */
    public String getWeatherCondition() { return weatherCondition; }

    /**
     * Returns the list of forecast days.
     *
     * @return an ordered list of {@link ForecastDay} entries
     */
    public List<ForecastDay> getForecast() { return forecast; }
}
