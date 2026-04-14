package fr.utc.miage.transpitrack;

import java.net.http.HttpClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TranspitrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TranspitrackApplication.class, args);
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}

}
