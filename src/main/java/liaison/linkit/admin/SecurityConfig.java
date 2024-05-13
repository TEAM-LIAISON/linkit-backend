package liaison.linkit.admin;

import liaison.linkit.admin.infrastructure.BCryptPasswordEncoder;
import liaison.linkit.admin.infrastructure.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
