package liaison.linkit.auth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
@Getter
public class AuthProperties {
    /**
     * 인증 모드: "header" (헤더 기반), "cookie" (쿠키 기반), "hybrid" (두 가지 모두 지원)
     */
    private String mode = "hybrid"; // 기본값

    /**
     * 마이그레이션 모드 활성화 여부
     */
    private boolean migrationEnabled = true;
}
