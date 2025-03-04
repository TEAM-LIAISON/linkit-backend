package liaison.linkit.global.config.swagger;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String API_TITLE = "LINKIT Backend API Docs";
    private static final String API_DESCRIPTION = "LINKIT Backend API 문서입니다.";
}
