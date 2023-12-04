package cz.cvut.ear.DarkstoreApi;

import cz.cvut.ear.DarkstoreApi.configuration.RateLimiterConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RateLimiterConfiguration.class)
public class DarkstoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DarkstoreApiApplication.class, args);
	}
}
