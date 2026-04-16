package fr.utc.miage.transpitrack.exception;

/**
 * Unchecked exception thrown by {@link fr.utc.miage.transpitrack.model.jpa.WeatherService}
 * when the weather data cannot be retrieved or parsed.
 * <p>
 * Wraps low-level I/O and parsing errors so callers do not need to handle
 * checked exceptions.
 * </p>
 */
public class WeatherServiceException extends RuntimeException {

    /**
     * Constructs a {@code WeatherServiceException} with a detail message and a cause.
     *
     * @param message a human-readable description of the error
     * @param cause   the underlying exception that triggered this one
     */
    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@code WeatherServiceException} with a detail message and no cause.
     *
     * @param message a human-readable description of the error
     */
    public WeatherServiceException(String message) {
        super(message);
    }
}
