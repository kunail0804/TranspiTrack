package fr.utc.miage.transpitrack.Dto;
import java.util.List;

public class WeatherResponse {
    private String city;
    private double currentTemp;
    private String weatherCondition;
    private List<ForecastDay> forecast; 

    public static class ForecastDay {
        private String date;
        private double minTemp;
        private double maxTemp;
        
        public ForecastDay(String date, double minTemp, double maxTemp) {
            this.date = date;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
        }

        public String getDate() {
            return date;
        }

        public double getMinTemp() {
            return minTemp;
        }

        public double getMaxTemp() {
            return maxTemp;
        }

        
    }

    public WeatherResponse(String city, double currentTemp, String weatherCondition, List<ForecastDay> forecast) {
        this.city = city;
        this.currentTemp = currentTemp;
        this.weatherCondition = weatherCondition;
        this.forecast = forecast;
    }

    // Getters
    public String getCity() { return city; }
    public double getCurrentTemp() { return currentTemp; }
    public String getWeatherCondition() { return weatherCondition; }
    public List<ForecastDay> getForecast() { return forecast; }


}