package cz.cvut.ear.DarkstoreApi.controller.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.ear.DarkstoreApi.dto.ErrorDetails;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final Bucket bucket;

    private final ObjectMapper objectMapper;

    @Autowired
    public RateLimitingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofSeconds(1))).withInitialTokens(10);
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "Too many requests.");

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(convertErrorDetailsToJson(errorDetails));
        }
    }

    private String convertErrorDetailsToJson(ErrorDetails errorDetails) throws JsonProcessingException {
        return objectMapper.writeValueAsString(errorDetails);
    }
}