package fr.utc.miage.transpitrack;

import java.net.http.HttpClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry point of the TranspiTrack Spring Boot application.
 * <p>
 * This class bootstraps the application context and exposes application-wide
 * Spring beans.
 * </p>
 */
@SpringBootApplication
public class TranspitrackApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the JVM
	 */
	public static void main(String[] args) {
		SpringApplication.run(TranspitrackApplication.class, args);
	}

	/**
	 * Provides a shared {@link HttpClient} bean used by services that call
	 * external HTTP APIs (e.g., the weather service).
	 *
	 * @return a new default {@link HttpClient} instance
	 */
	@Bean
	public HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}

}
