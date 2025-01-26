package liaison.linkit.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://liaison-dev.site",
                        "https://www.liaison-dev.site",
                        "https://www.linkit.im",
                        "https://linkit.im"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION);

        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
