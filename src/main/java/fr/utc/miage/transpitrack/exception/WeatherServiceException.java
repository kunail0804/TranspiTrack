package fr.utc.miage.transpitrack.exception;

public class WeatherServiceException extends RuntimeException {
    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherServiceException(String message) {
        super(message);
    }
}