package cz.cvut.ear.DarkstoreApi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
@Data
@Primary
public class RateLimiterConfiguration {
    private int capacity;
    private int refillTokens;
    private int refillDurationInSeconds;
    private int initialTokens;
}
