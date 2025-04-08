package liaison.linkit;

import java.util.TimeZone;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition
public class LinkitApplication {
    public static void main(String[] args) {

        // 애플리케이션 시작 시 JVM 기본 타임존을 Asia/Seoul로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(LinkitApplication.class, args);
    }
}
