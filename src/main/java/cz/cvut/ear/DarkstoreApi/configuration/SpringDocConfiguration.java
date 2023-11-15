package cz.cvut.ear.DarkstoreApi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringDocConfiguration implements WebMvcConfigurer {

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Darkstore API")
                .description("OpenAPI definition for Darkstore API that allows developers to integrate with the " +
                        "Darkstore service, which provides online grocery shopping and delivery.\n 2023")
                .version("1.0"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    }
}
