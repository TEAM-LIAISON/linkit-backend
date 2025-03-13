package liaison.linkit.global.config;

import liaison.linkit.login.infrastructure.AuthTokenMigrationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final AuthTokenMigrationInterceptor authTokenMigrationInterceptor;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://liaison-dev.site",
                        "https://www.liaison-dev.site",
                        "https://www.linkit.im",
                        "https://linkit.im")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*") // 모든 요청 헤더 허용
                .allowCredentials(true) // 쿠키 허용 (필수)
                .exposedHeaders(
                        HttpHeaders.LOCATION, HttpHeaders.SET_COOKIE); // Set-Cookie 헤더 노출 추가
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authTokenMigrationInterceptor)
                .addPathPatterns("/api/**"); // 모든 API에 적용
    }
}
