package liaison.linkit.global.config;

import java.util.List;

import liaison.linkit.auth.resolver.CurrentMemberIdArgumentResolver;
import liaison.linkit.global.filter.AccessorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CurrentMemberIdArgumentResolver currentMemberIdArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentMemberIdArgumentResolver);
    }

    @Bean
    public FilterRegistrationBean<AccessorFilter> accessorFilterRegistration(
            AccessorFilter accessorFilter) {
        FilterRegistrationBean<AccessorFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(accessorFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 인증 필터는 앞에서 수행되어야 함
        return registration;
    }

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
}
